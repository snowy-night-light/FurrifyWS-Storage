package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import ws.furrify.posts.post.dto.query.PostDetailsQueryDTO;
import ws.furrify.posts.post.dto.vo.PostQuerySearchDTO;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional(rollbackFor = RuntimeException.class)
interface SqlPostRepository extends Repository<PostSnapshot, Long> {
    PostSnapshot save(PostSnapshot postSnapshot);

    void deleteByPostId(UUID postId);

    boolean existsByOwnerIdAndPostId(UUID ownerId, UUID postId);

    Optional<PostSnapshot> findByOwnerIdAndPostId(UUID ownerId, UUID postId);

    @Query("from PostSnapshot post join post.tags tag where tag.value = ?2 and post.ownerId = ?1")
    Set<PostSnapshot> findAllByOwnerIdAndValueInTags(UUID ownerId, String value);

    @Query("from PostSnapshot post join post.artists artist where artist.artistId = ?2 and post.ownerId = ?1")
    Set<PostSnapshot> findAllByOwnerIdAndArtistIdInArtists(UUID ownerId, UUID artistId);

    @Query("from PostSnapshot post join post.mediaSet media where media.mediaId = ?3 and post.postId = ?2 and post.ownerId = ?1")
    Optional<PostSnapshot> findByOwnerIdAndPostIdAndMediaId(UUID ownerId, UUID postId, UUID mediaId);
}

@Transactional(rollbackFor = {})
interface SqlPostQueryRepositoryImpl extends PostQueryRepository, Repository<PostSnapshot, Long> {

    @Override
    Optional<PostDetailsQueryDTO> findByOwnerIdAndPostId(UUID ownerId, UUID postId);

    @Override
    Page<PostDetailsQueryDTO> findAllByOwnerId(UUID ownerId, Pageable pageable);

    @Override
    @Query("select post from PostSnapshot post join post.artists artist where artist.artistId = ?2 and post.ownerId = ?1")
    Page<PostDetailsQueryDTO> findAllByOwnerIdAndArtistId(UUID ownerId, UUID artistId, Pageable pageable);

    @Override
    @Query("select post from PostSnapshot post join post.artists artist join post.tags tag join post.mediaSet media where " +
            // Check if user if matches
            "post.ownerId = :#{#ownerId}" +
            " and " +
            // Check if required artists are present or if required artists are size 0 then ignore
            "(:#{#query.withArtists.size()} = 0 or artist.preferredNickname in (:#{#query.withArtists}))" +
            " and " +
            // Check if excluded artists are present or if excluded artists are size 0 then ignore
            "(:#{#query.withoutArtists.size()} = 0 or artist.preferredNickname not in (:#{#query.withoutArtists}))" +
            " and " +
            // Check if required media extensions are present or if required media extensions are size 0 then ignore
            "(:#{#query.withMediaExtensions.size()} = 0 or media.extension in (:#{#query.withMediaExtensions}))" +
            " and " +
            // Check if excluded media extensions are present or if excluded media extensions are size 0 then ignore
            "(:#{#query.withoutMediaExtensions.size()} = 0 or media.extension not in (:#{#query.withoutMediaExtensions}))" +
            " and " +
            // Check if required tags are present or if required tags are size 0 then ignore
            "(:#{#query.withTags.size()} = 0 or tag.value in (:#{#query.withTags}))" +
            " and " +
            // Check if excluded artists are present or if excluded tags are size 0 then ignore
            "(:#{#query.withoutTags.size()} = 0 or tag.value not in (:#{#query.withoutTags}))")
    Page<PostDetailsQueryDTO> findAllByOwnerIdAndQuery(UUID ownerId,
                                                       PostQuerySearchDTO query,
                                                       Pageable pageable);

    @Override
    @Query("select id from PostSnapshot where postId = ?1")
    Long getIdByPostId(UUID postId);
}

@org.springframework.stereotype.Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class PostRepositoryImpl implements PostRepository {

    private final SqlPostRepository sqlPostRepository;

    @Override
    public Post save(final Post post) {
        return Post.restore(sqlPostRepository.save(post.getSnapshot()));
    }

    @Override
    public Set<Post> findAllByOwnerIdAndValueInTags(final UUID ownerId, final String value) {
        return sqlPostRepository.findAllByOwnerIdAndValueInTags(ownerId, value)
                .stream()
                .map(Post::restore)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean existsByOwnerIdAndPostId(final UUID ownerId, final UUID postId) {
        return sqlPostRepository.existsByOwnerIdAndPostId(ownerId, postId);
    }

    @Override
    public Optional<Post> findByOwnerIdAndPostId(final UUID ownerId, final UUID postId) {
        return sqlPostRepository.findByOwnerIdAndPostId(ownerId, postId).map(Post::restore);
    }

    @Override
    public void deleteByPostId(final UUID postId) {
        sqlPostRepository.deleteByPostId(postId);
    }

    @Override
    public Set<Post> findAllByOwnerIdAndArtistIdInArtists(final UUID ownerId, final UUID artistId) {
        return sqlPostRepository.findAllByOwnerIdAndArtistIdInArtists(ownerId, artistId)
                .stream()
                .map(Post::restore)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Optional<Post> findByOwnerIdAndPostIdAndMediaId(final UUID ownerId, final UUID postId, final UUID mediaId) {
        return sqlPostRepository.findByOwnerIdAndPostIdAndMediaId(ownerId, postId, mediaId).map(Post::restore);
    }
}