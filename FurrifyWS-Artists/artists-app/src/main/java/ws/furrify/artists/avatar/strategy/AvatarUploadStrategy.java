package ws.furrify.artists.avatar.strategy;

import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.UUID;

/**
 * Strategy should implement way to upload the file to remote location.
 *
 * @author sky
 */
public interface AvatarUploadStrategy {

    UploadedAvatarFile uploadAvatarWithGeneratedThumbnail(final UUID avatarId, final MultipartFile fileSource);

    void removeAllAvatarFiles(UUID avatarId);

    @Value
    class UploadedAvatarFile {
        URI fileUri;
        URI thumbnailUri;
    }

}
