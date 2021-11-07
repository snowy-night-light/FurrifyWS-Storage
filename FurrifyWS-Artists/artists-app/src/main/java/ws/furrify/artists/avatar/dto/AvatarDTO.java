package ws.furrify.artists.avatar.dto;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import ws.furrify.artists.avatar.AvatarExtension;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author Skyte
 */
@Builder(toBuilder = true)
@Value
@ToString
public class AvatarDTO {
    Long id;

    UUID avatarId;
    UUID artistId;
    UUID ownerId;

    String filename;

    AvatarExtension extension;

    URL fileUrl;
    URL thumbnailUrl;

    String md5;

    ZonedDateTime createDate;
}
