package ws.furrify.artists.avatar.dto.query;

import ws.furrify.artists.avatar.AvatarExtension;

import java.io.Serializable;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface AvatarDetailsQueryDTO extends Serializable {

    UUID getAvatarId();

    UUID getArtistId();

    UUID getOwnerId();

    String getFilename();

    AvatarExtension getExtension();

    URI getFileUri();

    URI getThumbnailUri();

    String getMd5();

    ZonedDateTime getCreateDate();
}
