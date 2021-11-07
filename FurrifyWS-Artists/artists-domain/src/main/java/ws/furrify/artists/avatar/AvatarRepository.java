package ws.furrify.artists.avatar;

import java.util.Optional;
import java.util.UUID;

interface AvatarRepository {
    Optional<Avatar> findByOwnerIdAndArtistIdAndAvatarId(UUID ownerId, UUID artistId, UUID avatarId);

    void deleteByOwnerIdAndAvatarId(UUID ownerId, UUID avatarId);

    void deleteByOwnerIdAndArtistId(UUID ownerId, UUID artistId);

    boolean existsByOwnerIdAndArtistIdAndAvatarId(UUID ownerId, UUID artistId, UUID avatarId);

    Avatar save(Avatar avatar);
}
