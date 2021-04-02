package ws.furrify.posts.media;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import ws.furrify.posts.media.dto.query.MediaDetailsQueryDTO;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional(rollbackFor = RuntimeException.class)
interface SqlMediaRepository extends Repository<MediaSnapshot, Long> {
    MediaSnapshot save(MediaSnapshot mediaSnapshot);

    void deleteByMediaId(UUID mediaId);

    Set<MediaSnapshot> findAllByOwnerIdAndPostId(UUID ownerId, UUID postId);

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
    public Set<Media> findAllByOwnerIdAndPostId(final UUID ownerId, final UUID postId) {
        return sqlMediaRepository.findAllByOwnerIdAndPostId(ownerId, postId).stream()
                .map(Media::restore)
                .collect(Collectors.toUnmodifiableSet());
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