package ws.furrify.artists.avatar;

import ws.furrify.artists.avatar.dto.query.AvatarDetailsQueryDTO;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface AvatarQueryRepository {

    Optional<AvatarDetailsQueryDTO> findByOwnerIdAndArtistIdAndAvatarId(UUID ownerId, UUID artistId, UUID avatarId);

    Long getIdByAvatarId(UUID avatarId);
}
