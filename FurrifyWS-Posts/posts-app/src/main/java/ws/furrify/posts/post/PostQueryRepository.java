package ws.furrify.posts.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ws.furrify.posts.post.dto.query.PostDetailsQueryDTO;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface PostQueryRepository {
    Optional<PostDetailsQueryDTO> findByPostIdAndOwnerId(UUID userId, UUID postId);

    Page<PostDetailsQueryDTO> findAllByOwnerId(UUID userId, Pageable pageable);
}
