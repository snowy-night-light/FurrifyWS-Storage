package ws.furrify.tags.tag;

import lombok.RequiredArgsConstructor;
import ws.furrify.shared.DomainEventPublisher;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.tags.TagEvent;
import ws.furrify.tags.vo.TagData;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
class DeleteTagAdapter implements DeleteTagPort {

    private final DomainEventPublisher<TagEvent> domainEventPublisher;
    private final TagRepository tagRepository;

    @Override
    public void deleteTag(final UUID userId, final String value) {
        if (!tagRepository.existsByOwnerIdAndValue(userId, value)) {
            throw new RecordNotFoundException(Errors.NO_TAG_FOUND.getErrorMessage(value));
        }

        // Publish delete tag event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.TAG,
                // Use userId as key
                userId,
                createTagEvent(userId, value)
        );
    }

    private TagEvent createTagEvent(final UUID ownerId, final String value) {
        return TagEvent.newBuilder()
                .setState(DomainEventPublisher.TagEventType.REMOVED.name())
                .setTagValue(value)
                .setDataBuilder(
                        TagData.newBuilder()
                                .setValue(value)
                                .setTitle("")
                                .setDescription("")
                                .setOwnerId(ownerId.toString())
                )
                .setOccurredOn(Instant.now().toEpochMilli())
                .build();
    }
}
