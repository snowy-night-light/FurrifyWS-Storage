package ws.furrify.posts.attachment;

import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.attachment.dto.AttachmentDTO;

import java.util.UUID;

interface CreateAttachmentPort {

    UUID createAttachment(UUID userId, UUID postId, AttachmentDTO attachmentDTO, MultipartFile attachmentFile);
}
