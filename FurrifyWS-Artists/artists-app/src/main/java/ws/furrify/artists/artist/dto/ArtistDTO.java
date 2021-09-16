package ws.furrify.artists.artist.dto;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import ws.furrify.artists.artist.vo.ArtistSource;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * @author Skyte
 */
@Builder(toBuilder = true)
@Value
@ToString
public class ArtistDTO {
    Long id;

    UUID artistId;
    UUID ownerId;

    Set<String> nicknames;

    String preferredNickname;

    Set<ArtistSource> sources;

    ZonedDateTime createDate;
}
