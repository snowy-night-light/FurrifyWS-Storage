package ws.furrify.artists.artist.dto.query;

import java.io.Serializable;
import java.net.URI;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface ArtistAvatarQueryDTO extends Serializable {

    UUID getAvatarId();

    URI getFileUri();

    String getThumbnailUri();

    String getExtension();
}
