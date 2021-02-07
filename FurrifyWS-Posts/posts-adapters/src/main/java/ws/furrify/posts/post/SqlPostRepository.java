package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import ws.furrify.posts.post.dto.query.PostDetailsQueryDTO;

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
}

@Transactional(rollbackFor = {})
interface SqlPostQueryRepository extends PostQueryRepository, Repository<PostSnapshot, Long> {

    @Override
    Optional<PostDetailsQueryDTO> findByOwnerIdAndPostId(UUID ownerId, UUID postId);

    @Override
    Page<PostDetailsQueryDTO> findAllByOwnerId(UUID ownerId, Pageable pageable);
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
}