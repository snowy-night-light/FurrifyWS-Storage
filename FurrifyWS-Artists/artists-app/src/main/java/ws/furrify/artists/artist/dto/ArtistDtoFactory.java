package ws.furrify.artists.artist.dto;

import ws.furrify.artists.ArtistEvent;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
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
        Long createDateMillis = artistEvent.getData().getCreateDate();
        ZonedDateTime createDate = null;

        if (createDateMillis != null) {
            createDate = new Date(createDateMillis).toInstant().atZone(ZoneId.systemDefault());
        }

        return ArtistDTO.builder()
                .id(artistEvent.getArtistId())
                .artistId(UUID.fromString(artistEvent.getArtistUUID()))
                .ownerId(key)
                .nicknames(new HashSet<>(artistEvent.getData().getNicknames()))
                .preferredNickname(artistEvent.getData().getPreferredNickname())
                .createDate(createDate)
                .build();
    }

}
