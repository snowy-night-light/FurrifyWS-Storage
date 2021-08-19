package ws.furrify.sources.source.dto;

import lombok.RequiredArgsConstructor;
import ws.furrify.sources.source.SourceEvent;
import ws.furrify.sources.source.SourceQueryRepository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Creates SourceDTO from SourceEvent.
 * `
 *
 * @author Skyte
 */
@RequiredArgsConstructor
public class SourceDtoFactory {

    private final SourceQueryRepository sourceQueryRepository;

    public SourceDTO from(UUID key, SourceEvent sourceEvent) {
        Instant createDateInstant = sourceEvent.getData().getCreateDate();
        ZonedDateTime createDate = null;

        if (createDateInstant != null) {
            createDate = createDateInstant.atZone(ZoneId.systemDefault());
        }

        var sourceId = UUID.fromString(sourceEvent.getSourceId());

        return SourceDTO.builder()
                .id(
                        sourceQueryRepository.getIdBySourceId(sourceId)
                )
                .sourceId(sourceId)
                .ownerId(key)
                .createDate(createDate)
                .build();
    }

}
