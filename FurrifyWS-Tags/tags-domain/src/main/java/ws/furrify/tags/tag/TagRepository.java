package ws.furrify.tags.tag;

import java.util.Optional;
import java.util.UUID;

interface TagRepository {
    Tag save(Tag tag);

    void deleteByValue(String value);

    boolean existsByOwnerIdAndValue(UUID ownerId, String value);

    Optional<Tag> findByOwnerIdAndValue(UUID userId, String value);
}
