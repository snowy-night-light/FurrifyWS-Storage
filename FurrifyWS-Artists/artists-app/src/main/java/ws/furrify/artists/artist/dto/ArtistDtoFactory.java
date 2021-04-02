package ws.furrify.artists.artist.dto;

import ws.furrify.artists.artist.ArtistEvent;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.UUID;

/**
 * Creates ArtistDTO from ArtistEvent.
 * `
 *
 * @author Skyte
 */
public class ArtistDtoFactory {


    public ArtistDTO from(UUID key, ArtistEvent artistEvent) {
        Instant createDateInstant = artistEvent.getData().getCreateDate();
        ZonedDateTime createDate = null;

        if (createDateInstant != null) {
            createDate = createDateInstant.atZone(ZoneId.systemDefault());
        }

        return ArtistDTO.builder()
                .artistId(UUID.fromString(artistEvent.getArtistId()))
                .ownerId(key)
                .nicknames(new HashSet<>(artistEvent.getData().getNicknames()))
                .preferredNickname(artistEvent.getData().getPreferredNickname())
                .createDate(createDate)
                .build();
    }

}
