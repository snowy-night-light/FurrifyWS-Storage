package ws.furrify.sources.source;

import ws.furrify.sources.source.dto.SourceDTO;

import java.time.ZonedDateTime;
import java.util.UUID;

final class SourceFactory {

    Source from(SourceDTO sourceDTO) {
        SourceSnapshot sourceSnapshot = SourceSnapshot.builder()
                .id(sourceDTO.getId())
                .sourceId(
                        sourceDTO.getSourceId() != null ? sourceDTO.getSourceId() : UUID.randomUUID()
                )
                .ownerId(sourceDTO.getOwnerId())
                .createDate(
                        sourceDTO.getCreateDate() != null ? sourceDTO.getCreateDate() : ZonedDateTime.now()
                )
                .build();

        return Source.restore(sourceSnapshot);
    }

}
