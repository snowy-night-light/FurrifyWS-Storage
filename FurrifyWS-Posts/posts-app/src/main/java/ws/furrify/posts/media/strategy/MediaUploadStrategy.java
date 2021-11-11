package ws.furrify.posts.media.strategy;

import lombok.Value;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.media.MediaExtension;

import java.net.URI;
import java.util.UUID;

public interface MediaUploadStrategy {

    UploadedMediaFile uploadMediaWithGeneratedThumbnail(final UUID mediaId, final MediaExtension extension, final MultipartFile fileSource);

    @Value
    class UploadedMediaFile {
        URI fileUrl;
        URI thumbnailUrl;
    }

}
