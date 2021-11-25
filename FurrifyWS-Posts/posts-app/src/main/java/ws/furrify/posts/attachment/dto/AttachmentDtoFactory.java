package ws.furrify.posts.attachment.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ws.furrify.posts.attachment.AttachmentEvent;
import ws.furrify.posts.attachment.AttachmentExtension;
import ws.furrify.posts.attachment.AttachmentQueryRepository;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Creates AttachmentDTO from AttachmentEvent.
 *
 * @author Skyte
 */
@RequiredArgsConstructor
public class AttachmentDtoFactory {

    private final AttachmentQueryRepository attachmentQueryRepository;

    @SneakyThrows
    public AttachmentDTO from(UUID key, AttachmentEvent attachmentEvent) {
        Instant createDateInstant = attachmentEvent.getData().getCreateDate();
        ZonedDateTime createDate = null;

        if (createDateInstant != null) {
            createDate = createDateInstant.atZone(ZoneId.systemDefault());
        }

        var attachmentId = UUID.fromString(attachmentEvent.getAttachmentId());

        return AttachmentDTO.builder()
                .id(
                        attachmentQueryRepository.getIdByAttachmentId(attachmentId)
                )
                .attachmentId(attachmentId)
                .postId(UUID.fromString(attachmentEvent.getData().getPostId()))
                .ownerId(key)
                .extension(
                        (attachmentEvent.getData().getExtension() != null) ?
                                AttachmentExtension.valueOf(attachmentEvent.getData().getExtension()) :
                                null
                )
                .filename(attachmentEvent.getData().getFilename())
                .fileUri(
                        (attachmentEvent.getData().getFileUri() != null) ?
                                new URI(attachmentEvent.getData().getFileUri()) :
                                null
                )
                .md5(attachmentEvent.getData().getMd5())
                .createDate(createDate)
                .build();
    }

}
