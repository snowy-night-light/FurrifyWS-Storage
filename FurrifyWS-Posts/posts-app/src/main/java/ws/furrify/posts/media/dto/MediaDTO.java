package ws.furrify.posts.media.dto;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import ws.furrify.posts.media.MediaExtension;
import ws.furrify.posts.media.vo.MediaSource;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * @author Skyte
 */
@Builder(toBuilder = true)
@Value
@ToString
public class MediaDTO {
    Long id;

    UUID mediaId;
    UUID postId;
    UUID ownerId;

    Integer priority;

    String filename;

    MediaExtension extension;

    URI fileUrl;
    URI thumbnailUrl;

    String md5;

    Set<MediaSource> sources;

    ZonedDateTime createDate;
}
