package ws.furrify.sources.source;

import java.util.Optional;
import java.util.UUID;

interface SourceRepository {
    Source save(Source source);

    void deleteBySourceId(UUID sourceId);

    Optional<Source> findByOwnerIdAndSourceId(UUID ownerId, UUID sourceId);

    boolean existsByOwnerIdAndSourceId(UUID ownerId, UUID sourceId);

    long countSourcesByUserId(UUID userId);
}
