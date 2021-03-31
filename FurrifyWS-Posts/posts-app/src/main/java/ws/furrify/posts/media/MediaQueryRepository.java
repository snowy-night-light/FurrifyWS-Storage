package ws.furrify.posts.media;

import ws.furrify.posts.media.dto.query.MediaDetailsQueryDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface MediaQueryRepository {

    Optional<MediaDetailsQueryDTO> findByOwnerIdAndPostIdAndMediaId(UUID ownerId, UUID artistId, UUID mediaId);

    List<MediaDetailsQueryDTO> findAllByOwnerIdAndPostId(UUID ownerId, UUID postId);
}
