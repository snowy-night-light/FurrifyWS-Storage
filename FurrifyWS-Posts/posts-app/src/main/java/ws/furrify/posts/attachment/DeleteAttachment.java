package ws.furrify.posts.attachment;

import java.util.UUID;

interface DeleteAttachment {
    void deleteAttachment(final UUID userId, final UUID postId, final UUID attachmentId);
}
