package ws.furrify.artists.avatar;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.artists.avatar.dto.AvatarDTO;
import ws.furrify.artists.avatar.dto.command.AvatarCreateCommandDTO;
import ws.furrify.artists.avatar.dto.command.AvatarReplaceCommandDTO;
import ws.furrify.artists.avatar.dto.command.AvatarUpdateCommandDTO;

import java.util.UUID;

@RestController
@Validated
@RequestMapping("/users/{userId}/artists/{artistId}/avatar")
@RequiredArgsConstructor
class CommandArtistAvatarController {

    private final AvatarFacade avatarFacade;

    @PostMapping
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('create_artist_avatar') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#jwtAuthenticationToken))"
    )
    public ResponseEntity<?> createAvatar(@PathVariable UUID userId,
                                          @PathVariable UUID artistId,
                                          @RequestPart("avatar") @Validated AvatarCreateCommandDTO avatarCreateCommandDTO,
                                          @RequestPart("file") MultipartFile mediaFile,
                                          JwtAuthenticationToken jwtAuthenticationToken,
                                          HttpServletResponse response) {
        AvatarDTO avatarDTO = avatarCreateCommandDTO.toDTO();

        response.addHeader("Id",
                avatarFacade.createAvatar(userId, artistId, avatarDTO, mediaFile).toString()
        );

        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{avatarId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('update_artist_avatar') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#jwtAuthenticationToken))"
    )
    public ResponseEntity<?> updateAvatar(@PathVariable UUID userId,
                                          @PathVariable UUID artistId,
                                          @PathVariable UUID avatarId,
                                          @RequestPart("avatar") @Validated AvatarUpdateCommandDTO avatarUpdateCommandDTO,
                                          @RequestPart(value = "file", required = false) MultipartFile avatarFile,
                                          JwtAuthenticationToken jwtAuthenticationToken) {

        avatarFacade.updateAvatar(userId, artistId, avatarId, avatarUpdateCommandDTO.toDTO(), avatarFile);

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/{avatarId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('replace_artist_avatar') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#jwtAuthenticationToken))"
    )
    public ResponseEntity<?> replaceAvatar(@PathVariable UUID userId,
                                           @PathVariable UUID artistId,
                                           @PathVariable UUID avatarId,
                                           @RequestPart("avatar") @Validated AvatarReplaceCommandDTO avatarReplaceCommandDTO,
                                           @RequestPart(value = "file") MultipartFile avatarFile,
                                           JwtAuthenticationToken jwtAuthenticationToken) {
        avatarFacade.replaceAvatar(userId, artistId, avatarId, avatarReplaceCommandDTO.toDTO(), avatarFile);

        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{avatarId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('delete_artist_avatar') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#jwtAuthenticationToken))"
    )
    public ResponseEntity<?> deleteAvatar(@PathVariable UUID userId,
                                          @PathVariable UUID artistId,
                                          @PathVariable UUID avatarId,
                                          JwtAuthenticationToken jwtAuthenticationToken) {
        avatarFacade.deleteAvatar(userId, artistId, avatarId);

        return ResponseEntity.accepted().build();
    }
}
