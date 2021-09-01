package ws.furrify.sources.source;

import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.sources.source.converter.SourceStrategyAttributeConverter;
import ws.furrify.sources.source.vo.SourceData;

import java.time.Instant;
import java.util.UUID;

/**
 * Utils class regarding Source entity.
 *
 * @author Skyte
 */
class SourceUtils {

    /**
     * Create SourceEvent by given aggregate and set its state change.
     *
     * @param eventType Event state change type.
     * @param source    Source aggregate to build source event from.
     * @return Created source event.
     */
    public static SourceEvent createSourceEvent(final DomainEventPublisher.SourceEventType eventType,
                                                final Source source,
                                                final SourceStrategyAttributeConverter sourceStrategyAttributeConverter) {
        SourceSnapshot sourceSnapshot = source.getSnapshot();

        return SourceEvent.newBuilder()
                .setState(eventType.name())
                .setSourceId(sourceSnapshot.getSourceId().toString())
                .setData(
                        SourceData.newBuilder()
                                .setOwnerId(sourceSnapshot.getOwnerId().toString())
                                .setStrategy(
                                        sourceStrategyAttributeConverter.convertToDatabaseColumn(
                                                sourceSnapshot.getStrategy()
                                        )
                                )
                                .setDataHashMap(sourceSnapshot.getData())
                                .setCreateDate(sourceSnapshot.getCreateDate().toInstant())
                                .build()
                )
                .setOccurredOn(Instant.now())
                .build();
    }

    /**
     * Create SourceEvent with REMOVE state.
     *
     * @param sourceId SourceId the delete event will regard.
     * @return Created source event.
     */
    public static SourceEvent deleteSourceEvent(final UUID sourceId) {
        return SourceEvent.newBuilder()
                .setState(DomainEventPublisher.SourceEventType.REMOVED.name())
                .setSourceId(sourceId.toString())
                .setDataBuilder(SourceData.newBuilder())
                .setOccurredOn(Instant.now())
                .build();
    }

}
