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
    private final MediaFile mediaFile;

    private final ZonedDateTime createDate;

    static Media restore(MediaSnapshot mediaSnapshot) {
        return new Media(
                mediaSnapshot.getId(),
                mediaSnapshot.getMediaId(),
                mediaSnapshot.getPostId(),
                mediaSnapshot.getOwnerId(),
                mediaSnapshot.getPriority(),
                mediaSnapshot.getMediaFile(),
                mediaSnapshot.getCreateDate()
        );
    }

    MediaSnapshot getSnapshot() {
        return MediaSnapshot.builder()
                .id(id)
                .mediaId(mediaId)
                .postId(postId)
                .ownerId(ownerId)
                .priority(priority)
                .mediaFile(mediaFile)
                .createDate(createDate)
                .build();
    }

    void replacePriority(MediaPriority mediaPriority) {
        this.priority = mediaPriority;
    }
}