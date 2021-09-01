package ws.furrify.sources.source.dto;

import lombok.RequiredArgsConstructor;
import ws.furrify.sources.source.SourceEvent;
import ws.furrify.sources.source.SourceQueryRepository;
import ws.furrify.sources.source.converter.SourceStrategyAttributeConverter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
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
    private final SourceStrategyAttributeConverter sourceStrategyAttributeConverter;

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
                .strategy(
                        sourceStrategyAttributeConverter.convertToEntityAttribute(
                                sourceEvent.getData().getStrategy()
                        )
                )
                .data(new HashMap<>(sourceEvent.getData().getDataHashMap()))
                .createDate(createDate)
                .build();
    }

}
