package ws.furrify.posts.media.strategy;

import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.UUID;

public interface MediaUploadStrategy {

    UploadedMediaFile uploadMediaWithGeneratedThumbnail(final UUID mediaId, final MultipartFile fileSource);

    @Value
    class UploadedMediaFile {
        URL fileUrl;
        URL thumbnailUrl;
    }

}
