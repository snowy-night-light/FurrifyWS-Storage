package ws.furrify.sources.posts.dto.query;

import lombok.Data;

import java.util.UUID;

/**
 * @author Skyte
 */
@Data
public class AttachmentDetailsQueryDTO {
    /**
     * Attachment UUID.
     */
    private UUID attachmentId;
}
