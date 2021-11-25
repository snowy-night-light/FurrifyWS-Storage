package ws.furrify.posts.attachment.dto.query;

import ws.furrify.posts.attachment.AttachmentExtension;
import ws.furrify.posts.attachment.vo.AttachmentSource;

import java.io.Serializable;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface AttachmentDetailsQueryDTO extends Serializable {

    UUID getAttachmentId();

    UUID getPostId();

    UUID getOwnerId();

    String getFilename();

    AttachmentExtension getExtension();

    URI getFileUri();

    String getMd5();

    Set<AttachmentSource> getSources();

    ZonedDateTime getCreateDate();
}
