package ws.furrify.posts.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ws.furrify.posts.media.vo.MediaSource;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Set;
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

    private Integer priority;

    private String filename;
    private String md5;

    private MediaExtension extension;

    private URL fileUrl;
    private URL thumbnailUrl;

    private Set<MediaSource> sources;

    private ZonedDateTime createDate;
}
