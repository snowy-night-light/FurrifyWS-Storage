package ws.furrify.sources.source;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ws.furrify.sources.source.dto.SourceDTO;
import ws.furrify.sources.source.dto.command.SourceCreateCommandDTO;
import ws.furrify.sources.source.dto.command.SourceReplaceCommandDTO;
import ws.furrify.sources.source.dto.command.SourceUpdateCommandDTO;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/users/{userId}/sources")
@RequiredArgsConstructor
class CommandSourceController {

    private final SourceFacade sourceFacade;

    @PostMapping
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('create_source') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public ResponseEntity<?> createSource(@PathVariable UUID userId,
                                          @RequestBody @Validated SourceCreateCommandDTO sourceCreateCommandDTO,
                                          KeycloakAuthenticationToken keycloakAuthenticationToken,
                                          HttpServletResponse response) {
        SourceDTO sourceDTO = sourceCreateCommandDTO.toDTO();

        response.addHeader("Id",
                sourceFacade.createSource(userId, sourceDTO).toString()
        );

        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{sourceId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('delete_source') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public ResponseEntity<?> deleteSource(@PathVariable UUID userId,
                                          @PathVariable UUID sourceId,
                                          KeycloakAuthenticationToken keycloakAuthenticationToken) {
        sourceFacade.deleteSource(userId, sourceId);

        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{sourceId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('update_source') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public ResponseEntity<?> updateSource(@PathVariable UUID userId,
                                          @PathVariable UUID sourceId,
                                          @RequestBody @Validated SourceUpdateCommandDTO sourceUpdateCommandDTO,
                                          KeycloakAuthenticationToken keycloakAuthenticationToken) {
        sourceFacade.updateSource(userId, sourceId, sourceUpdateCommandDTO.toDTO());

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/{sourceId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('update_source') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public ResponseEntity<?> replaceSource(@PathVariable UUID userId,
                                           @PathVariable UUID sourceId,
                                           @RequestBody @Validated SourceReplaceCommandDTO sourceReplaceCommandDTO,
                                           KeycloakAuthenticationToken keycloakAuthenticationToken) {
        sourceFacade.replaceSource(userId, sourceId, sourceReplaceCommandDTO.toDTO());

        return ResponseEntity.accepted().build();
    }

}
