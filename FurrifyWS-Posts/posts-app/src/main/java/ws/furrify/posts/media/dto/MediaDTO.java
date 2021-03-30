package ws.furrify.posts.media.dto;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import ws.furrify.posts.media.MediaExtension;
import ws.furrify.posts.media.MediaStatus;

import java.net.URL;
import java.time.ZonedDateTime;
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

    URL fileUrl;
    URL thumbnailUrl;

    String fileHash;

    MediaStatus status;

    ZonedDateTime createDate;
}
