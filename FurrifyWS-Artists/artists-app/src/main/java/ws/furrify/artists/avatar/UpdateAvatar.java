package ws.furrify.artists.avatar;

import org.springframework.web.multipart.MultipartFile;
import ws.furrify.artists.avatar.dto.AvatarDTO;

import java.util.UUID;

interface UpdateAvatar {

    void updateAvatar(final UUID userId,
                      final UUID artistId,
                      final UUID avatarId,
                      final AvatarDTO avatarDTO,
                      final MultipartFile avatarFile);

}
