package ws.furrify.sources.source;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ws.furrify.shared.vo.SourceOriginType;
import ws.furrify.sources.source.dto.SourceDTO;
import ws.furrify.sources.source.dto.command.SourceCreateCommandDTO;

import java.util.UUID;

@RestController
@Validated
@RequestMapping("/users/{userId}/posts/{postId}/media/{mediaId}/sources")
@RequiredArgsConstructor
class CommandMediaSourceController {

    private final SourceFacade sourceFacade;
    private final CommandSourceControllerUtils commandSourceControllerUtils;

    @PostMapping
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('create_media_source') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#jwtAuthenticationToken))"
    )
    public ResponseEntity<?> createMediaSource(@PathVariable UUID userId,
                                               @PathVariable UUID postId,
                                               @PathVariable UUID mediaId,
                                               @RequestBody @Validated SourceCreateCommandDTO sourceCreateCommandDTO,
                                               JwtAuthenticationToken jwtAuthenticationToken,
                                               HttpServletResponse response) {

        commandSourceControllerUtils.checkForSourceHardLimit(userId);

        SourceDTO sourceDTO = sourceCreateCommandDTO.toDTO();

        // Add values from path
        sourceDTO = sourceDTO.toBuilder()
                .ownerId(userId)
                .postId(postId)
                .originId(mediaId)
                .originType(SourceOriginType.MEDIA)
                .build();

        response.addHeader("Id",
                sourceFacade.createSource(userId, sourceDTO).toString()
        );

        return ResponseEntity.accepted().build();
    }

}
