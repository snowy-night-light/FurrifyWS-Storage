package ws.furrify.artists.avatar;

import java.util.UUID;

interface DeleteAvatar {
    void deleteAvatar(final UUID userId, final UUID artistId, final UUID avatarId);
}
