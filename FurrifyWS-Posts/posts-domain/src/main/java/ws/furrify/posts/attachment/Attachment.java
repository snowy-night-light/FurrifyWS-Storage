package ws.furrify.posts.attachment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.java.Log;
import ws.furrify.posts.attachment.vo.AttachmentFile;

import java.time.ZonedDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
@Log
class Attachment {
    private final Long id;
    @NonNull
    private final UUID attachmentId;
    @NonNull
    private final UUID postId;
    @NonNull
    private final UUID ownerId;
    @NonNull
    private final AttachmentFile file;

    private final ZonedDateTime createDate;

    static Attachment restore(AttachmentSnapshot attachmentSnapshot) {
        return new Attachment(
                attachmentSnapshot.getId(),
                attachmentSnapshot.getAttachmentId(),
                attachmentSnapshot.getPostId(),
                attachmentSnapshot.getOwnerId(),
                AttachmentFile.builder()
                        .extension(attachmentSnapshot.getExtension())
                        .filename(attachmentSnapshot.getFilename())
                        .md5(attachmentSnapshot.getMd5())
                        .fileUrl(attachmentSnapshot.getFileUrl())
                        .build(),
                attachmentSnapshot.getCreateDate()
        );
    }

    AttachmentSnapshot getSnapshot() {
        return AttachmentSnapshot.builder()
                .id(id)
                .attachmentId(attachmentId)
                .postId(postId)
                .ownerId(ownerId)
                .extension(file.getExtension())
                .filename(file.getFilename())
                .md5(file.getMd5())
                .fileUrl(file.getFileUrl())
                .createDate(createDate)
                .build();
    }
}