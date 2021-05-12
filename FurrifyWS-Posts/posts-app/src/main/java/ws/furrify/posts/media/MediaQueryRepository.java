package ws.furrify.posts.media;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ws.furrify.posts.media.dto.query.MediaDetailsQueryDTO;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface MediaQueryRepository {

    Optional<MediaDetailsQueryDTO> findByOwnerIdAndPostIdAndMediaId(UUID ownerId, UUID artistId, UUID mediaId);

    Page<MediaDetailsQueryDTO> findAllByOwnerIdAndPostId(UUID ownerId, UUID postId, Pageable pageable);

    Long getIdByMediaId(UUID mediaId);
}
