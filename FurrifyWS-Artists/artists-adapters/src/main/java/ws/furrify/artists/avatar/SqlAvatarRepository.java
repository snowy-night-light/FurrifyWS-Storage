package ws.furrify.artists.avatar;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import ws.furrify.artists.avatar.dto.query.AvatarDetailsQueryDTO;

import java.util.Optional;
import java.util.UUID;

@Transactional(rollbackFor = RuntimeException.class)
interface SqlAvatarRepository extends Repository<AvatarSnapshot, Long> {
    AvatarSnapshot save(AvatarSnapshot avatarSnapshot);

    void deleteByOwnerIdAndAvatarId(UUID ownerId, UUID avatarId);

    void deleteByOwnerIdAndArtistId(UUID ownerId, UUID artistId);

    boolean existsByOwnerIdAndArtistIdAndAvatarId(UUID ownerId, UUID artistId, UUID avatarId);

    Optional<AvatarSnapshot> findByOwnerIdAndArtistIdAndAvatarId(UUID ownerId, UUID artistId, UUID avatarId);
}

@Transactional(rollbackFor = {})
interface SqlAvatarQueryRepositoryImpl extends AvatarQueryRepository, Repository<AvatarSnapshot, Long> {

    @Override
    Optional<AvatarDetailsQueryDTO> findByOwnerIdAndArtistIdAndAvatarId(UUID ownerId, UUID artistId, UUID avatarId);

    @Override
    @Query("select id from AvatarSnapshot where artistId = ?1")
    Long getIdByAvatarId(UUID avatarId);
}

@org.springframework.stereotype.Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class AvatarRepositoryImpl implements AvatarRepository {

    private final SqlAvatarRepository sqlAvatarRepository;

    @Override
    public Avatar save(final Avatar avatar) {
        return Avatar.restore(sqlAvatarRepository.save(avatar.getSnapshot()));
    }

    @Override
    public Optional<Avatar> findByOwnerIdAndArtistIdAndAvatarId(final UUID ownerId, final UUID artistId, final UUID avatarId) {
        return sqlAvatarRepository.findByOwnerIdAndArtistIdAndAvatarId(ownerId, artistId, avatarId).map(Avatar::restore);
    }

    @Override
    public void deleteByOwnerIdAndAvatarId(final UUID ownerId, final UUID avatarId) {
        sqlAvatarRepository.deleteByOwnerIdAndAvatarId(ownerId, avatarId);
    }

    @Override
    public void deleteByOwnerIdAndArtistId(final UUID ownerId, final UUID artistId) {
        sqlAvatarRepository.deleteByOwnerIdAndArtistId(ownerId, artistId);
    }

    @Override
    public boolean existsByOwnerIdAndArtistIdAndAvatarId(final UUID ownerId, final UUID artistId, final UUID avatarId) {
        return sqlAvatarRepository.existsByOwnerIdAndArtistIdAndAvatarId(ownerId, artistId, avatarId);
    }


}