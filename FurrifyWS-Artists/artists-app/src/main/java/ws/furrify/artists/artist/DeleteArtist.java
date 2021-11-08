package ws.furrify.artists.artist;

import java.util.UUID;

interface DeleteArtist {
    void deleteArtist(final UUID ownerId, final UUID artistId);
}
