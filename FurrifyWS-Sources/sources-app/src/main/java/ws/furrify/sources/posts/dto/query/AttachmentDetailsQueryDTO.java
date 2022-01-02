package ws.furrify.sources.posts.dto.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * @author Skyte
 */
@Data
@AllArgsConstructor(onConstructor_ = @JsonCreator)
public class AttachmentDetailsQueryDTO {
    /**
     * Attachment UUID.
     */
    private UUID attachmentId;
}
