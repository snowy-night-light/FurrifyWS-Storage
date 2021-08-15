package ws.furrify.posts.media;

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
            "hasRole('admin') ||" +
                    "(hasRole('create_post_media') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public ResponseEntity<?> createMedia(@PathVariable UUID userId,
                                         @PathVariable UUID postId,
                                         @RequestPart("media") @Validated MediaCreateCommandDTO mediaCreateCommandDTO,
                                         @RequestPart("file") MultipartFile mediaFile,
                                         KeycloakAuthenticationToken keycloakAuthenticationToken,
                                         HttpServletResponse response) {
        MediaDTO mediaDTO = mediaCreateCommandDTO.toDTO();

        response.addHeader("Id",
                mediaFacade.createMedia(userId, postId, mediaDTO, mediaFile).toString()
        );

        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{mediaId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('delete_post_media') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public ResponseEntity<?> deleteMedia(@PathVariable UUID userId,
                                         @PathVariable UUID postId,
                                         @PathVariable UUID mediaId,
                                         KeycloakAuthenticationToken keycloakAuthenticationToken) {
        mediaFacade.deleteMedia(userId, postId, mediaId);

        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{mediaId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('update_post_media') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public ResponseEntity<?> updateMedia(@PathVariable UUID userId,
                                         @PathVariable UUID postId,
                                         @PathVariable UUID mediaId,
                                         @RequestBody @Validated MediaUpdateCommandDTO mediaUpdateCommandDTO,
                                         KeycloakAuthenticationToken keycloakAuthenticationToken) {
        mediaFacade.updateMedia(userId, postId, mediaId, mediaUpdateCommandDTO.toDTO());

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/{mediaId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('replace_post_media') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public ResponseEntity<?> replaceMedia(@PathVariable UUID userId,
                                          @PathVariable UUID postId,
                                          @PathVariable UUID mediaId,
                                          @RequestBody @Validated MediaReplaceCommandDTO mediaReplaceCommandDTO,
                                          KeycloakAuthenticationToken keycloakAuthenticationToken) {
        mediaFacade.replaceMedia(userId, postId, mediaId, mediaReplaceCommandDTO.toDTO());

        return ResponseEntity.accepted().build();
    }

}
