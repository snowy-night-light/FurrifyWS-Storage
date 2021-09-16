package ws.furrify.sources.source;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ws.furrify.sources.source.dto.query.SourceDetailsQueryDTO;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface SourceQueryRepository {
    Optional<SourceDetailsQueryDTO> findByOwnerIdAndSourceId(UUID ownerId, UUID sourceId);

    Page<SourceDetailsQueryDTO> findAllByOwnerId(UUID ownerId, Pageable pageable);

    Long getIdBySourceId(UUID sourceId);
}
