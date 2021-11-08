package ws.furrify.posts.attachment;

import ws.furrify.posts.attachment.dto.AttachmentDTO;

import java.util.UUID;

interface ReplaceAttachment {

    void replaceAttachment(final UUID userId, final UUID postId, final UUID attachmentId, final AttachmentDTO attachmentDTO);

}
