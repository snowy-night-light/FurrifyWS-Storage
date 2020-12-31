package ws.furrify.posts.tag;

import ws.furrify.posts.tag.dto.query.TagDetailsQueryDTO;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface TagQueryRepository {
    Optional<TagDetailsQueryDTO> findByValue(UUID userId, String value);

    Set<TagDetailsQueryDTO> findAll(UUID userId);
}
