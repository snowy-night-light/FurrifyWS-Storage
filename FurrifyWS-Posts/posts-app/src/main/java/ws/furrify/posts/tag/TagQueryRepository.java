package ws.furrify.posts.tag;

import ws.furrify.posts.tag.dto.query.TagDetailsQueryDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface TagQueryRepository {
    Optional<TagDetailsQueryDTO> findByOwnerIdAndValue(UUID userId, String value);

    List<TagDetailsQueryDTO> findAllByOwnerId(UUID userId);
}
