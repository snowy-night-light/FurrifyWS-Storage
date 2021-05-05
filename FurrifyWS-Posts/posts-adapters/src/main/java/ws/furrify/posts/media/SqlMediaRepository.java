package ws.furrify.posts.media;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import ws.furrify.posts.media.dto.query.MediaDetailsQueryDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional(rollbackFor = RuntimeException.class)
interface SqlMediaRepository extends Repository<MediaSnapshot, Long> {
    MediaSnapshot save(MediaSnapshot mediaSnapshot);

    void deleteByMediaId(UUID mediaId);

    Optional<MediaSnapshot> findByOwnerIdAndPostIdAndMediaId(UUID ownerId, UUID postId, UUID mediaId);

    boolean existsByOwnerIdAndPostIdAndMediaId(UUID ownerId, UUID postId, UUID mediaId);
}

@Transactional(rollbackFor = {})
interface SqlMediaQueryRepository extends MediaQueryRepository, Repository<MediaSnapshot, Long> {

    @Override
    Optional<MediaDetailsQueryDTO> findByOwnerIdAndPostIdAndMediaId(UUID ownerId, UUID artistId, UUID mediaId);

    @Override
    @Query("from MediaSnapshot where ownerId = ?1 and postId = ?2 order by priority desc")
    List<MediaDetailsQueryDTO> findAllByOwnerIdAndPostId(UUID ownerId, UUID postId);

    @Override
    @Query("select id from MediaSnapshot where mediaId = ?1")
    Long getIdByMediaId(UUID mediaId);
}

@org.springframework.stereotype.Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MediaRepositoryImpl implements MediaRepository {

    private final SqlMediaRepository sqlMediaRepository;

    @Override
    public Media save(final Media media) {
        return Media.restore(
                sqlMediaRepository.save(media.getSnapshot())
        );
    }

    @Override
    public Optional<Media> findByOwnerIdAndPostIdAndMediaId(final UUID ownerId, final UUID postId, final UUID mediaId) {
        return sqlMediaRepository.findByOwnerIdAndPostIdAndMediaId(ownerId, postId, mediaId)
                .map(Media::restore);
    }

    @Override
    public void deleteByMediaId(final UUID mediaId) {
        sqlMediaRepository.deleteByMediaId(mediaId);
    }

    @Override
    public boolean existsByOwnerIdAndPostIdAndMediaId(final UUID ownerId, final UUID postId, final UUID mediaId) {
        return sqlMediaRepository.existsByOwnerIdAndPostIdAndMediaId(ownerId, postId, mediaId);
    }
}