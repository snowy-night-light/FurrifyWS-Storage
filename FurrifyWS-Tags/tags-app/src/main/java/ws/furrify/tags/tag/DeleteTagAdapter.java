package ws.furrify.tags.tag;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.UUID;

@RequiredArgsConstructor
class DeleteTagAdapter implements DeleteTagPort {

    private final DomainEventPublisher<TagEvent> domainEventPublisher;
    private final TagRepository tagRepository;

    @Override
    public void deleteTag(@NonNull final UUID userId,
                          @NonNull final String value) {
        if (!tagRepository.existsByOwnerIdAndValue(userId, value)) {
            throw new RecordNotFoundException(Errors.NO_TAG_FOUND.getErrorMessage(value));
        }

        // Publish delete tag event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.TAG,
                // Use userId as key
                userId,
                TagUtils.deleteTagEvent(
                        userId,
                        value
                )
        );
    }
}
