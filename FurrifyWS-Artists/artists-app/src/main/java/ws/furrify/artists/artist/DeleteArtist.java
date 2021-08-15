package ws.furrify.artists.artist;

import java.util.UUID;

interface DeleteArtist {
    void deleteArtist(UUID ownerId, UUID artistId);
}
