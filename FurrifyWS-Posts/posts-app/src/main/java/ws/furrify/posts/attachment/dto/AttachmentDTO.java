package ws.furrify.posts.attachment.dto;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import ws.furrify.posts.attachment.AttachmentExtension;
import ws.furrify.posts.attachment.vo.AttachmentSource;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Set;
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

    URI fileUrl;

    String md5;

    Set<AttachmentSource> sources;

    ZonedDateTime createDate;
}
