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
import java.util.UUID;

@Transactional(rollbackFor = RuntimeException.class)
interface SqlPostRepository extends Repository<PostSnapshot, Long> {
    PostSnapshot save(PostSnapshot postSnapshot);

    void deleteByPostId(UUID postId);

    boolean existsByOwnerIdAndPostId(UUID ownerId, UUID postId);

    Optional<PostSnapshot> findByOwnerIdAndPostId(UUID ownerId, UUID postId);
}

/* PROJECTIONS ARE DONE MANUALLY CAUSE FOR WHATEVER REASON
CLASS BASED PROJECTIONS DON'T WORK IN THIS PROJECT */

@Transactional(rollbackFor = {})
interface SqlPostQueryRepository extends PostQueryRepository, Repository<PostSnapshot, Long> {

    @Override
    @Query(value = "select " +
            "new ws.furrify.posts.post.dto.query.PostDetailsQueryDTO(" +
            "post.postId, " +
            "post.ownerId, " +
            "post.title, " +
            "post.description, " +
            "post.createDate" +
            ")" +
            " from PostSnapshot post where post.postId = ?2 and post.ownerId = ?1")
    Optional<PostDetailsQueryDTO> findByPostId(UUID userId, UUID postId);

    @Override
    @Query(value = "select " +
            "new ws.furrify.posts.post.dto.query.PostDetailsQueryDTO(" +
            "post.postId, " +
            "post.ownerId, " +
            "post.title, " +
            "post.description, " +
            "post.createDate" +
            ")" +
            " from PostSnapshot post where post.ownerId = ?1")
    Page<PostDetailsQueryDTO> findAll(UUID userId, Pageable pageable);
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