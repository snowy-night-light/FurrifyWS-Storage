package ws.furrify.sources.source;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ws.furrify.shared.vo.SourceOriginType;
import ws.furrify.sources.source.dto.SourceDTO;
import ws.furrify.sources.source.dto.command.SourceCreateCommandDTO;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/users/{userId}/artists/{artistId}/sources")
@RequiredArgsConstructor
class CommandArtistSourceController {

    private final SourceFacade sourceFacade;

    @PostMapping
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('create_artist_source') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public ResponseEntity<?> createArtistSource(@PathVariable UUID userId,
                                                @PathVariable UUID artistId,
                                                @RequestBody @Validated SourceCreateCommandDTO sourceCreateCommandDTO,
                                                KeycloakAuthenticationToken keycloakAuthenticationToken,
                                                HttpServletResponse response) {
        SourceDTO sourceDTO = sourceCreateCommandDTO.toDTO();

        // Add values from path
        sourceDTO = sourceDTO.toBuilder()
                .ownerId(userId)
                .originId(artistId)
                .originType(SourceOriginType.ARTIST)
                .build();

        response.addHeader("Id",
                sourceFacade.createSource(userId, sourceDTO).toString()
        );

        return ResponseEntity.accepted().build();
    }

}
