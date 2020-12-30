package ws.furrify.posts.post;

import java.util.UUID;

interface DeletePostPort {
    void deletePost(final UUID userId, final UUID postId);
}
