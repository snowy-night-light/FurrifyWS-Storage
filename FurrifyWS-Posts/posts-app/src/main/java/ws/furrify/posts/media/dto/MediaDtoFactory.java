package ws.furrify.posts.media.dto;

import lombok.SneakyThrows;
import ws.furrify.posts.media.MediaEvent;
import ws.furrify.posts.media.MediaExtension;
import ws.furrify.posts.media.MediaStatus;

import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Creates PostDTO from PostEvent.
 * `
 *
 * @author Skyte
 */
public class MediaDtoFactory {

    @SneakyThrows
    public MediaDTO from(UUID key, MediaEvent mediaEvent) {
        Instant createDateInstant = mediaEvent.getData().getCreateDate();
        ZonedDateTime createDate = null;

        if (createDateInstant != null) {
            createDate = createDateInstant.atZone(ZoneId.systemDefault());
        }

        return MediaDTO.builder()
                .mediaId(UUID.fromString(mediaEvent.getMediaId()))
                .postId(UUID.fromString(mediaEvent.getData().getPostId()))
                .ownerId(key)
                .priority(mediaEvent.getData().getPriority())
                .extension(
                        MediaExtension.valueOf(mediaEvent.getData().getExtension())
                )
                .filename(mediaEvent.getData().getFilename())
                .fileUrl(new URL(mediaEvent.getData().getFileUrl()))
                .fileHash(mediaEvent.getData().getFileHash())
                .thumbnailUrl(new URL(mediaEvent.getData().getThumbnailUrl()))
                .status(
                        MediaStatus.valueOf(mediaEvent.getData().getStatus())
                )
                .createDate(createDate)
                .build();
    }

}
