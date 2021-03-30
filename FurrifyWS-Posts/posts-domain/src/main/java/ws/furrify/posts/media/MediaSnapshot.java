package ws.furrify.posts.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ws.furrify.posts.media.vo.MediaFile;
import ws.furrify.posts.media.vo.MediaPriority;

import java.time.ZonedDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@EqualsAndHashCode
@ToString
@Getter(value = PACKAGE)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Builder(access = PACKAGE)
class MediaSnapshot {
    private Long id;

    private UUID mediaId;
    private UUID postId;
    private UUID ownerId;

    private MediaPriority priority;

    private MediaFile mediaFile;

    private ZonedDateTime createDate;
}
