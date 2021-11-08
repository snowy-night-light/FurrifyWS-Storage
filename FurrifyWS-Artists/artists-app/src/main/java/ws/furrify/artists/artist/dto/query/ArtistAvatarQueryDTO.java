package ws.furrify.artists.artist.dto.query;

import java.io.Serializable;
import java.net.URL;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface ArtistAvatarQueryDTO extends Serializable {

    UUID getAvatarId();

    URL getFileUrl();

    String getThumbnailUrl();

    String getExtension();
}
