package ws.furrify.sources.source;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ws.furrify.sources.source.dto.command.SourceReplaceCommandDTO;
import ws.furrify.sources.source.dto.command.SourceUpdateCommandDTO;

import java.util.UUID;

@RestController
@Validated
@RequestMapping("/users/{userId}/sources")
@RequiredArgsConstructor
class CommandSourceController {

    private final SourceFacade sourceFacade;

    @DeleteMapping("/{sourceId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "hasAuthority(@keycloakConfig.clientId + '_admin') or " +
                    "(hasAuthority(@keycloakConfig.clientId + '_delete_source') && #userId.equals(@jwtAuthorizationUtilsImpl.getCurrentUserId(#jwtAuthenticationToken)))"
    )
    public ResponseEntity<?> deleteSource(@PathVariable UUID userId,
                                          @PathVariable UUID sourceId,
                                          JwtAuthenticationToken jwtAuthenticationToken) {
        sourceFacade.deleteSource(userId, sourceId);

        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{sourceId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "hasAuthority(@keycloakConfig.clientId + '_admin') or " +
                    "(hasAuthority(@keycloakConfig.clientId + '_update_source') && #userId.equals(@jwtAuthorizationUtilsImpl.getCurrentUserId(#jwtAuthenticationToken)))"
    )
    public ResponseEntity<?> updateSource(@PathVariable UUID userId,
                                          @PathVariable UUID sourceId,
                                          @RequestBody @Validated SourceUpdateCommandDTO sourceUpdateCommandDTO,
                                          JwtAuthenticationToken jwtAuthenticationToken) {
        sourceFacade.updateSource(userId, sourceId, sourceUpdateCommandDTO.toDTO());

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/{sourceId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "hasAuthority(@keycloakConfig.clientId + '_admin') or " +
                    "(hasAuthority(@keycloakConfig.clientId + '_replace_source') && #userId.equals(@jwtAuthorizationUtilsImpl.getCurrentUserId(#jwtAuthenticationToken)))"
    )
    public ResponseEntity<?> replaceSource(@PathVariable UUID userId,
                                           @PathVariable UUID sourceId,
                                           @RequestBody @Validated SourceReplaceCommandDTO sourceReplaceCommandDTO,
                                           JwtAuthenticationToken jwtAuthenticationToken) {
        sourceFacade.replaceSource(userId, sourceId, sourceReplaceCommandDTO.toDTO());

        return ResponseEntity.accepted().build();
    }

}
