package ws.furrify.sources.posts.dto.query;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * @author Skyte
 */
@Data
@AllArgsConstructor
public class MediaDetailsQueryDTO {
    /**
     * Media UUID.
     */
    private UUID mediaId;
}
