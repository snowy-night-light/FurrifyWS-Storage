package ws.furrify.posts.attachment.strategy;

import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.UUID;

public interface AttachmentUploadStrategy {

    UploadedAttachmentFile uploadAttachment(final UUID attachmentId, final MultipartFile fileSource);

    @Value
    class UploadedAttachmentFile {
        URL fileUrl;
    }

}
