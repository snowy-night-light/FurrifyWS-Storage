package ws.furrify.posts.media.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ws.furrify.posts.media.MediaEvent;
import ws.furrify.posts.media.MediaExtension;
import ws.furrify.posts.media.MediaQueryRepository;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Creates MediaDTO from MediaEvent.
 *
 * @author Skyte
 */
@RequiredArgsConstructor
public class MediaDtoFactory {

    private final MediaQueryRepository mediaQueryRepository;

    @SneakyThrows
    public MediaDTO from(UUID key, MediaEvent mediaEvent) {
        Instant createDateInstant = mediaEvent.getData().getCreateDate();
        ZonedDateTime createDate = null;

        if (createDateInstant != null) {
            createDate = createDateInstant.atZone(ZoneId.systemDefault());
        }

        var mediaId = UUID.fromString(mediaEvent.getMediaId());

        return MediaDTO.builder()
                .id(
                        mediaQueryRepository.getIdByMediaId(mediaId)
                )
                .mediaId(mediaId)
                .postId(UUID.fromString(mediaEvent.getData().getPostId()))
                .ownerId(key)
                .priority(mediaEvent.getData().getPriority())
                .extension(
                        (mediaEvent.getData().getExtension() != null) ?
                                MediaExtension.valueOf(mediaEvent.getData().getExtension()) :
                                null
                )
                .filename(mediaEvent.getData().getFilename())
                .fileUri(
                        (mediaEvent.getData().getFileUri() != null) ?
                                new URI(mediaEvent.getData().getFileUri()) :
                                null
                )
                .md5(mediaEvent.getData().getMd5())
                .thumbnailUri(
                        (mediaEvent.getData().getThumbnailUri() != null) ?
                                new URI(mediaEvent.getData().getThumbnailUri()) :
                                null

                )
                .createDate(createDate)
                .build();
    }

}
