package ws.furrify.posts.media;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.java.Log;
import ws.furrify.posts.media.vo.MediaFile;
import ws.furrify.posts.media.vo.MediaPriority;

import java.time.ZonedDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
@Log
class Media {
    private final Long id;
    @NonNull
    private final UUID mediaId;
    @NonNull
    private final UUID postId;
    @NonNull
    private final UUID ownerId;
    @NonNull
    private MediaPriority priority;
    @NonNull
    private final MediaFile file;

    private final ZonedDateTime createDate;

    static Media restore(MediaSnapshot mediaSnapshot) {
        return new Media(
                mediaSnapshot.getId(),
                mediaSnapshot.getMediaId(),
                mediaSnapshot.getPostId(),
                mediaSnapshot.getOwnerId(),
                MediaPriority.of(mediaSnapshot.getPriority()),
                MediaFile.builder()
                        .extension(mediaSnapshot.getExtension())
                        .thumbnailUrl(mediaSnapshot.getThumbnailUrl())
                        .filename(mediaSnapshot.getFilename())
                        .fileHash(mediaSnapshot.getFileHash())
                        .fileUrl(mediaSnapshot.getFileUrl())
                        .build(),
                mediaSnapshot.getCreateDate()
        );
    }

    MediaSnapshot getSnapshot() {
        return MediaSnapshot.builder()
                .id(id)
                .mediaId(mediaId)
                .postId(postId)
                .ownerId(ownerId)
                .priority(priority.getPriority())
                .extension(file.getExtension())
                .thumbnailUrl(file.getThumbnailUrl())
                .filename(file.getFilename())
                .fileHash(file.getFileHash())
                .fileUrl(file.getFileUrl())
                .status(file.getStatus())
                .createDate(createDate)
                .build();
    }

    void replacePriority(MediaPriority mediaPriority) {
        this.priority = mediaPriority;
    }
}