package ws.furrify.posts.post;

import java.util.Optional;
import java.util.UUID;

interface PostRepository {
    Post save(Post post);

    boolean existsByOwnerIdAndPostId(UUID ownerId, UUID postId);

    Optional<Post> findByOwnerIdAndPostId(UUID ownerId, UUID postId);

    void deleteByPostId(UUID postId);
}
