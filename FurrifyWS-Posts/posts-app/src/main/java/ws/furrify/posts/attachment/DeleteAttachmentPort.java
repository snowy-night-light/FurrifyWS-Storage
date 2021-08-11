package ws.furrify.posts.attachment;

import java.util.UUID;

interface DeleteAttachmentPort {
    void deleteAttachment(final UUID userId, final UUID postId, final UUID attachmentId);
}
