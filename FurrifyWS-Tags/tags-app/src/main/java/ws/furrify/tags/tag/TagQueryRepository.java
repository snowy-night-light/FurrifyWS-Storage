package ws.furrify.tags.tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ws.furrify.tags.tag.dto.query.TagDetailsQueryDTO;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface TagQueryRepository {
    Optional<TagDetailsQueryDTO> findByOwnerIdAndValue(UUID userId, String value);

    Page<TagDetailsQueryDTO> findAllByOwnerIdAndLikeMatch(UUID userId, String match, Pageable pageable);

    Long getIdByValue(String value);
}
