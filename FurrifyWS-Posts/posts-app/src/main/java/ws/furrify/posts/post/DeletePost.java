package ws.furrify.posts.post;

import java.util.UUID;

interface DeletePost {
    void deletePost(final UUID userId, final UUID postId);
}
