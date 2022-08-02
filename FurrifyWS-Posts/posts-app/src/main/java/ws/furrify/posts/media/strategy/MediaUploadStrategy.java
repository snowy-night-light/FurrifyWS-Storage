package ws.furrify.posts.media.strategy;

import lombok.Value;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.media.MediaExtension;

import java.net.URI;
import java.util.UUID;

/**
 * Strategy should implement way to upload the file to remote location.
 *
 * @author sky
 */
public interface MediaUploadStrategy {

    UploadedMediaFile uploadMediaWithGeneratedThumbnail(final UUID mediaId, final MediaExtension extension, final MultipartFile fileSource);

    UploadedMediaFile uploadMedia(final UUID mediaId, final MediaExtension extension, final MultipartFile fileSource, final MultipartFile thumbnailSource);

    UploadedMediaFile uploadThumbnail(final UUID mediaId, final String originalMediaFilename, final MultipartFile thumbnailFile);

    @Value
    class UploadedMediaFile {
        URI fileUri;
        URI thumbnailUri;
    }

}
