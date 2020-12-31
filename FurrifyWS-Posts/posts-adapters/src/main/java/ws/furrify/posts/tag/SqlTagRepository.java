package ws.furrify.posts.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import ws.furrify.posts.tag.dto.query.TagDetailsQueryDTO;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Transactional(rollbackFor = RuntimeException.class)
interface SqlTagRepository extends Repository<TagSnapshot, Long> {
    TagSnapshot save(TagSnapshot tagSnapshot);

    boolean existsByValue(String value);
}

/* PROJECTIONS ARE DONE MANUALLY CAUSE FOR WHATEVER REASON
CLASS BASED PROJECTIONS DON'T WORK IN THIS PROJECT */

@Transactional(rollbackFor = {})
interface SqlTagQueryRepository extends TagQueryRepository, Repository<TagSnapshot, Long> {

    @Override
    @Query(value = "select " +
            "new ws.furrify.posts.tag.dto.query.TagDetailsQueryDTO(" +
            "tag.value, " +
            "tag.ownerId, " +
            "tag.type, " +
            "tag.createDate" +
            ")" +
            " from TagSnapshot tag where tag.value = ?2 and tag.ownerId = ?1")
    Optional<TagDetailsQueryDTO> findByValue(UUID userId, String value);

    @Override
    @Query(value = "select " +
            "new ws.furrify.posts.tag.dto.query.TagDetailsQueryDTO(" +
            "tag.value, " +
            "tag.ownerId, " +
            "tag.type, " +
            "tag.createDate" +
            ")" +
            " from TagSnapshot tag where tag.ownerId = ?1")
    Set<TagDetailsQueryDTO> findAll(UUID userId);
}

@org.springframework.stereotype.Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class TagRepositoryImpl implements TagRepository {

    private final SqlTagRepository sqlTagRepository;

    @Override
    public Tag save(final Tag tag) {
        return Tag.restore(sqlTagRepository.save(tag.getSnapshot()));
    }

    @Override
    public boolean existsByValue(final String value) {
        return sqlTagRepository.existsByValue(value);
    }
}