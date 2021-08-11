package ws.furrify.posts.post;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

interface PostRepository {
    Post save(Post post);

    Set<Post> findAllByOwnerIdAndValueInTags(UUID ownerId, String value);

    boolean existsByOwnerIdAndPostId(UUID ownerId, UUID postId);

    Optional<Post> findByOwnerIdAndPostId(UUID ownerId, UUID postId);

    void deleteByPostId(UUID postId);

    Set<Post> findAllByOwnerIdAndArtistIdInArtists(UUID ownerId, UUID artistId);

    Optional<Post> findByOwnerIdAndPostIdAndMediaId(UUID ownerId, UUID postId, UUID mediaId);

    Optional<Post> findByOwnerIdAndPostIdAndAttachmentId(UUID ownerId, UUID postId, UUID attachment);
}
