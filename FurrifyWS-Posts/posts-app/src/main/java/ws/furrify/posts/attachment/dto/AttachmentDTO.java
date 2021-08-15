package ws.furrify.posts.attachment.dto;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import ws.furrify.posts.attachment.AttachmentExtension;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author Skyte
 */
@Builder(toBuilder = true)
@Value
@ToString
public class AttachmentDTO {
    Long id;

    UUID attachmentId;
    UUID postId;
    UUID ownerId;

    String filename;

    AttachmentExtension extension;

    URL fileUrl;

    String md5;

    ZonedDateTime createDate;
}
