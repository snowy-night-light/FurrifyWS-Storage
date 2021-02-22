package ws.furrify.artists.artist;

import java.util.UUID;

interface DeleteArtistPort {
    void deleteArtist(UUID ownerId, UUID artistId);
}
