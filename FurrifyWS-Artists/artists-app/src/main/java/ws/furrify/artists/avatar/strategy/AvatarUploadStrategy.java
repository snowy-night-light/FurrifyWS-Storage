package ws.furrify.artists.avatar.strategy;

import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.UUID;

public interface AvatarUploadStrategy {

    UploadedAvatarFile uploadAvatarWithGeneratedThumbnail(final UUID artistId, final UUID avatarId, final MultipartFile fileSource);

    @Value
    class UploadedAvatarFile {
        URI fileUrl;
        URI thumbnailUrl;
    }

}
