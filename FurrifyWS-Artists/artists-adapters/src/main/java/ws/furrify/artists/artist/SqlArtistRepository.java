package ws.furrify.artists.artist;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import ws.furrify.artists.artist.dto.query.ArtistDetailsQueryDTO;

import java.util.Optional;
import java.util.UUID;

@Transactional(rollbackFor = RuntimeException.class)
interface SqlArtistRepository extends Repository<ArtistSnapshot, Long> {
    ArtistSnapshot save(ArtistSnapshot artistSnapshot);

    void deleteByArtistId(UUID artistId);

    boolean existsByOwnerIdAndPreferredNickname(UUID ownerId, String preferredNickname);

    boolean existsByOwnerIdAndArtistId(UUID ownerId, UUID artistId);

    Optional<ArtistSnapshot> findByOwnerIdAndArtistId(UUID ownerId, UUID artistId);
}

@Transactional(rollbackFor = {})
interface SqlArtistQueryRepositoryImpl extends ArtistQueryRepository, Repository<ArtistSnapshot, Long> {

    @Override
    Optional<ArtistDetailsQueryDTO> findByOwnerIdAndArtistId(UUID ownerId, UUID artistId);

    @Override
    Page<ArtistDetailsQueryDTO> findAllByOwnerId(UUID ownerId, Pageable pageable);

    @Override
    @Query("select id from ArtistSnapshot where artistId = ?1")
    Long getIdByArtistId(UUID artistId);
}

@org.springframework.stereotype.Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ArtistRepositoryImpl implements ArtistRepository {

    private final SqlArtistRepository sqlArtistRepository;

    @Override
    public Artist save(final Artist artist) {
        return Artist.restore(sqlArtistRepository.save(artist.getSnapshot()));
    }

    @Override
    public void deleteByArtistId(final UUID artistId) {
        sqlArtistRepository.deleteByArtistId(artistId);
    }

    @Override
    public boolean existsByOwnerIdAndPreferredNickname(final UUID ownerId, final String preferredNickname) {
        return sqlArtistRepository.existsByOwnerIdAndPreferredNickname(ownerId, preferredNickname);
    }

    @Override
    public Optional<Artist> findByOwnerIdAndArtistId(final UUID ownerId, final UUID artistId) {
        return sqlArtistRepository.findByOwnerIdAndArtistId(ownerId, artistId).map(Artist::restore);
    }

    @Override
    public boolean existsByOwnerIdAndArtistId(final UUID ownerId, final UUID artistId) {
        return sqlArtistRepository.existsByOwnerIdAndArtistId(ownerId, artistId);
    }


}