package ws.furrify.sources.posts.dto.query;

import lombok.Data;

import java.util.UUID;

/**
 * @author Skyte
 */
@Data
public class MediaDetailsQueryDTO {
    /**
     * Media UUID.
     */
    private UUID mediaId;
}
