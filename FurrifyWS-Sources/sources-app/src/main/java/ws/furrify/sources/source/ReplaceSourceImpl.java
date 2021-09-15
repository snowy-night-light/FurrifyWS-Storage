package ws.furrify.sources.source;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.sources.source.converter.SourceStrategyAttributeConverter;
import ws.furrify.sources.source.dto.SourceDTO;

import java.util.UUID;

@RequiredArgsConstructor
final class ReplaceSourceImpl implements ReplaceSource {

    private final SourceRepository sourceRepository;
    private final DomainEventPublisher<SourceEvent> eventPublisher;
    private final SourceStrategyAttributeConverter sourceStrategyAttributeConverter;

    @Override
    public void replaceSource(@NonNull final UUID ownerId,
                              @NonNull final UUID sourceId,
                              @NonNull final SourceDTO sourceDTO) {
        Source source = sourceRepository.findByOwnerIdAndSourceId(ownerId, sourceId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(sourceId.toString())));

        source.updateData(sourceDTO.getData(), sourceDTO.getStrategy());

        // Publish create source event
        eventPublisher.publish(
                DomainEventPublisher.Topic.ARTIST,
                // Use ownerId as key
                ownerId,
                SourceUtils.createSourceEvent(
                        DomainEventPublisher.SourceEventType.REPLACED,
                        source,
                        sourceStrategyAttributeConverter
                )
        );

    }
}
