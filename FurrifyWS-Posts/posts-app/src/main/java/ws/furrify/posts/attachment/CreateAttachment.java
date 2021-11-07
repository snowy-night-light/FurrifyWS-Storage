package ws.furrify.posts.attachment;

import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.attachment.dto.AttachmentDTO;

import java.util.UUID;

interface CreateAttachment {

    UUID createAttachment(final UUID userId, final UUID postId, final AttachmentDTO attachmentDTO, final MultipartFile attachmentFile);
}
