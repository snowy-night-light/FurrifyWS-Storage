package ws.furrify.posts.attachment.strategy;

import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.UUID;

/**
 * Strategy should implement way to upload the file to remote location.
 *
 * @author sky
 */
public interface AttachmentUploadStrategy {

    UploadedAttachmentFile uploadAttachment(final UUID attachmentId, final MultipartFile fileSource);

    void removeAllAttachmentFiles(UUID attachmentId);

    @Value
    class UploadedAttachmentFile {
        URI fileUri;
    }

}
