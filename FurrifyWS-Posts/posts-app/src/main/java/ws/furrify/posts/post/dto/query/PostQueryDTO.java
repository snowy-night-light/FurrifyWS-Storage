package ws.furrify.posts.post.dto.query;

import java.io.Serializable;
import java.util.UUID;

/**
 * Interface for Post DTO queries.
 *
 * @author Skyte
 */
interface PostQueryDTO extends Serializable {
    UUID getPostId();
}
