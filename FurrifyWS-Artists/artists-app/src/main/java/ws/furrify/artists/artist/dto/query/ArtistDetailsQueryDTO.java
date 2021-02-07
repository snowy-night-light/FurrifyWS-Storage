package ws.furrify.artists.artist.dto.query;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface ArtistDetailsQueryDTO extends Serializable {

    Long getId();

    UUID getArtistId();

    UUID getOwnerId();

    Set<String> getNicknames();

    String getPreferredNickname();

    ZonedDateTime getCreateDate();

}
