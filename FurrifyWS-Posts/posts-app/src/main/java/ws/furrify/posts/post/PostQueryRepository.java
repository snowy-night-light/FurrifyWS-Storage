package ws.furrify.posts.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ws.furrify.posts.post.dto.query.PostDetailsQueryDTO;
import ws.furrify.posts.post.dto.vo.PostQuerySearchDTO;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface PostQueryRepository {
    Optional<PostDetailsQueryDTO> findByOwnerIdAndPostId(UUID ownerId, UUID postId);

    Page<PostDetailsQueryDTO> findAllByOwnerId(UUID ownerId, Pageable pageable);

    Page<PostDetailsQueryDTO> findAllByOwnerIdAndArtistId(UUID userId, UUID artistId, Pageable pageable);

    Page<PostDetailsQueryDTO> findAllByOwnerIdAndQuery(
            UUID ownerId,
            PostQuerySearchDTO query,
            Pageable pageable
    );

    Long getIdByPostId(UUID postId);
}
