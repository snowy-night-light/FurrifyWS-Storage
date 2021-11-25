package ws.furrify.posts.attachment;

import ws.furrify.posts.attachment.dto.AttachmentDTO;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.UUID;

class AttachmentFactory {

    Attachment from(AttachmentDTO attachmentDTO) {
        AttachmentSnapshot attachmentSnapshot = AttachmentSnapshot.builder()
                .id(attachmentDTO.getId())
                .attachmentId(
                        (attachmentDTO.getAttachmentId() != null) ? attachmentDTO.getAttachmentId() : UUID.randomUUID()
                )
                .postId(attachmentDTO.getPostId())
                .ownerId(attachmentDTO.getOwnerId())
                .filename(attachmentDTO.getFilename())
                .md5(attachmentDTO.getMd5())
                .extension(attachmentDTO.getExtension())
                .fileUri(attachmentDTO.getFileUri())
                .sources(
                        (attachmentDTO.getSources() != null) ? attachmentDTO.getSources() : new HashSet<>()
                )
                .createDate(
                        (attachmentDTO.getCreateDate() != null) ? attachmentDTO.getCreateDate() : ZonedDateTime.now()
                )
                .build();

        return Attachment.restore(attachmentSnapshot);
    }

}
