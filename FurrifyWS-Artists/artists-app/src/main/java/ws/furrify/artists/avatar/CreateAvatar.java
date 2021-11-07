package ws.furrify.artists.avatar;

import org.springframework.web.multipart.MultipartFile;
import ws.furrify.artists.avatar.dto.AvatarDTO;

import java.util.UUID;

interface CreateAvatar {

    UUID createAvatar(final UUID userId, final UUID artistId, final AvatarDTO avatarDTO, final MultipartFile avatarFile);
}
