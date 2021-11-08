package ws.furrify.artists.avatar;

import java.util.UUID;

interface AvatarRepository {

    boolean existsByOwnerIdAndArtistId(UUID ownerId, UUID artistId);

    void deleteByOwnerIdAndAvatarId(UUID ownerId, UUID avatarId);

    void deleteByOwnerIdAndArtistId(UUID ownerId, UUID artistId);

    boolean existsByOwnerIdAndArtistIdAndAvatarId(UUID ownerId, UUID artistId, UUID avatarId);

    Avatar save(Avatar avatar);
}
