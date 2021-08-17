package ws.furrify.artists.artist;

import ws.furrify.artists.artist.dto.ArtistDTO;

import java.util.UUID;

interface CreateArtist {
    UUID createArtist(UUID ownerId, ArtistDTO artistDTO);
}
