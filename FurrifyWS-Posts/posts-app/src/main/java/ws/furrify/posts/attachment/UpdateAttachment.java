package ws.furrify.posts.attachment;

import ws.furrify.posts.attachment.dto.AttachmentDTO;

import java.util.UUID;

interface UpdateAttachment {

    void updateAttachment(UUID userId, UUID postId, UUID attachmentId, AttachmentDTO attachmentDTO);

}
