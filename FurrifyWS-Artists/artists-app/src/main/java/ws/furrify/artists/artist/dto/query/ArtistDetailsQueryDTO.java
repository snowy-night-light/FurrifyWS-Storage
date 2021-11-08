package ws.furrify.artists.artist.dto.query;

import ws.furrify.artists.artist.vo.ArtistSource;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface ArtistDetailsQueryDTO extends Serializable {

    UUID getArtistId();

    UUID getOwnerId();

    Set<String> getNicknames();

    String getPreferredNickname();

    Set<ArtistSource> getSources();

    ArtistAvatarQueryDTO getAvatar();

    ZonedDateTime getCreateDate();

}
