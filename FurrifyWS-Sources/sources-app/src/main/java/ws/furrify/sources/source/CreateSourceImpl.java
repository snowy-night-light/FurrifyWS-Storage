package ws.furrify.sources.source;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.sources.source.converter.SourceStrategyAttributeConverter;
import ws.furrify.sources.source.dto.SourceDTO;

import java.time.ZonedDateTime;
import java.util.UUID;

@RequiredArgsConstructor
final class CreateSourceImpl implements CreateSource {

    private final SourceFactory sourceFactory;
    private final DomainEventPublisher<SourceEvent> eventPublisher;
    private final SourceStrategyAttributeConverter sourceStrategyAttributeConverter;

    @Override
    public UUID createSource(@NonNull final UUID ownerId,
                             @NonNull final SourceDTO sourceDTO) {
        // Generate source UUID
        UUID sourceId = UUID.randomUUID();

        // Update sourceDTO and create Source from that data
        Source source = sourceFactory.from(
                sourceDTO.toBuilder()
                        .sourceId(sourceId)
                        .ownerId(ownerId)
                        .strategy(sourceDTO.getStrategy())
                        .data(sourceDTO.getData())
                        .createDate(ZonedDateTime.now())
                        .build()
        );

        // Publish create source event
        eventPublisher.publish(
                DomainEventPublisher.Topic.SOURCE,
                // Use ownerId as key
                ownerId,
                SourceUtils.createSourceEvent(
                        DomainEventPublisher.SourceEventType.CREATED,
                        source,
                        sourceStrategyAttributeConverter
                )
        );


        return sourceId;
    }
}
