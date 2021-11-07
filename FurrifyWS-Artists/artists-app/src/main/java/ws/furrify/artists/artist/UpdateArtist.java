package ws.furrify.artists.artist;

import ws.furrify.artists.artist.dto.ArtistDTO;

import java.util.UUID;

interface UpdateArtist {
    void updateArtist(final UUID ownerId, final UUID artistId, final ArtistDTO artistDTO);
}
