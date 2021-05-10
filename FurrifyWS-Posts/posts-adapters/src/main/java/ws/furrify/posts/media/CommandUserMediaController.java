package ws.furrify.posts.media;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.media.dto.MediaDTO;
import ws.furrify.posts.media.dto.command.MediaCreateCommandDTO;
import ws.furrify.posts.media.dto.command.MediaReplaceCommandDTO;
import ws.furrify.posts.media.dto.command.MediaUpdateCommandDTO;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/users/{userId}/posts/{postId}/media")
@RequiredArgsConstructor
class CommandUserMediaController {

    private final MediaFacade mediaFacade;

    @PostMapping
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public ResponseEntity<?> createMedia(@PathVariable UUID userId,
                                         @PathVariable UUID postId,
                                         @RequestPart("media") @Validated MediaCreateCommandDTO mediaCreateCommandDTO,
                                         // FIXME Make file required
                                         @RequestPart("file") MultipartFile mediaFile,
                                         @AuthenticationPrincipal KeycloakAuthenticationToken keycloakAuthenticationToken,
                                         HttpServletResponse response) {
        MediaDTO mediaDTO = mediaCreateCommandDTO.toDTO();

        response.addHeader("Id",
                mediaFacade.createMedia(userId, postId, mediaDTO, mediaFile).toString()
        );

        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{mediaId}")
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public ResponseEntity<?> deleteMedia(@PathVariable UUID userId,
                                         @PathVariable UUID postId,
                                         @PathVariable UUID mediaId,
                                         @AuthenticationPrincipal KeycloakAuthenticationToken keycloakAuthenticationToken) {
        mediaFacade.deleteMedia(userId, postId, mediaId);

        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{mediaId}")
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public ResponseEntity<?> updateMedia(@PathVariable UUID userId,
                                         @PathVariable UUID postId,
                                         @PathVariable UUID mediaId,
                                         @RequestBody @Validated MediaUpdateCommandDTO mediaUpdateCommandDTO,
                                         @AuthenticationPrincipal KeycloakAuthenticationToken keycloakAuthenticationToken) {
        mediaFacade.updateMedia(userId, postId, mediaId, mediaUpdateCommandDTO.toDTO());

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/{mediaId}")
    @PreAuthorize(
            "hasRole('admin') or " +
                    "hasAuthority('admin') or " +
                    "(#keycloakAuthenticationToken != null and #userId == #keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject())"
    )
    public ResponseEntity<?> replaceMedia(@PathVariable UUID userId,
                                          @PathVariable UUID postId,
                                          @PathVariable UUID mediaId,
                                          @RequestBody @Validated MediaReplaceCommandDTO mediaReplaceCommandDTO,
                                          @AuthenticationPrincipal KeycloakAuthenticationToken keycloakAuthenticationToken) {
        mediaFacade.replaceMedia(userId, postId, mediaId, mediaReplaceCommandDTO.toDTO());

        return ResponseEntity.accepted().build();
    }

}
