package ws.furrify.posts.media;

import java.util.UUID;

interface DeleteMediaPort {
    void deleteMedia(final UUID userId, final UUID postId, final UUID mediaId);
}
