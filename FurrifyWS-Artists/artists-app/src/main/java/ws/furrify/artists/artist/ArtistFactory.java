package ws.furrify.artists.artist;

import ws.furrify.artists.artist.dto.ArtistDTO;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.UUID;

class ArtistFactory {

    Artist from(ArtistDTO artistDTO) {
        ArtistSnapshot artistSnapshot = ArtistSnapshot.builder()
                .id(artistDTO.getId())
                .artistId(
                        artistDTO.getArtistId() != null ? artistDTO.getArtistId() : UUID.randomUUID()
                )
                .ownerId(artistDTO.getOwnerId())
                .nicknames(
                        artistDTO.getNicknames() != null ? artistDTO.getNicknames() : new HashSet<>()
                )
                .createDate(
                        artistDTO.getCreateDate() != null ? artistDTO.getCreateDate() : ZonedDateTime.now()
                )
                .preferredNickname(artistDTO.getPreferredNickname())
                .build();

        return Artist.restore(artistSnapshot);
    }

}
