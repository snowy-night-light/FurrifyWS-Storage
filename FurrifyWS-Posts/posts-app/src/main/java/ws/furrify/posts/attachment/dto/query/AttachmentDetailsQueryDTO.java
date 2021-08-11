package ws.furrify.posts.attachment.dto.query;

import ws.furrify.posts.attachment.AttachmentExtension;

import java.io.Serializable;
import java.net.URL;
import java.time.ZonedDateTime;
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

    URL getFileUrl();

    String getMd5();

    ZonedDateTime getCreateDate();
}
