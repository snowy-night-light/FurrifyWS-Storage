package ws.furrify.posts.media;

import java.util.Optional;
import java.util.UUID;

interface MediaRepository {
    Optional<Media> findByOwnerIdAndPostIdAndMediaId(UUID ownerId, UUID postId, UUID mediaId);

    void deleteByMediaId(UUID mediaId);

    boolean existsByOwnerIdAndPostIdAndMediaId(UUID ownerId, UUID postId, UUID mediaId);

    Media save(Media media);

    long countMediaByUserId(UUID userId);
}
