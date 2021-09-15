package ws.furrify.sources.source;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.UUID;

@RequiredArgsConstructor
final class DeleteSourceImpl implements DeleteSource {

    private final SourceRepository sourceRepository;
    private final DomainEventPublisher<SourceEvent> domainEventPublisher;

    @Override
    public void deleteSource(@NonNull final UUID ownerId,
                             @NonNull final UUID sourceId) {
        if (!sourceRepository.existsByOwnerIdAndSourceId(ownerId, sourceId)) {
            throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(sourceId.toString()));
        }

        // Publish delete source event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.SOURCE,
                // Use ownerId as key
                ownerId,
                SourceUtils.deleteSourceEvent(sourceId)
        );
    }
}
