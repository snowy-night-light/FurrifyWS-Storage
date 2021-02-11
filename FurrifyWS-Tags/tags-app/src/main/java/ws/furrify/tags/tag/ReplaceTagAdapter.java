package ws.furrify.tags.tag;

import lombok.RequiredArgsConstructor;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.tags.tag.dto.TagDTO;
import ws.furrify.tags.tag.vo.TagData;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
class ReplaceTagAdapter implements ReplaceTagPort {

    private final DomainEventPublisher<TagEvent> domainEventPublisher;
    private final TagRepository tagRepository;

    @Override
    public void replaceTag(final UUID userId, final String value, final TagDTO tagDTO) {
        Tag tag = tagRepository.findByOwnerIdAndValue(userId, value)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_TAG_FOUND.getErrorMessage(value)));

        // Update fields in tag
        tag.updateValue(tagDTO.getValue(), tagRepository);
        tag.updateType(tagDTO.getType());

        // Publish replace tag event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.TAG,
                // User userId as key
                userId,
                createTagEvent(value, tag)
        );
    }

    private TagEvent createTagEvent(final String oldTagValue, final Tag tag) {
        TagSnapshot tagSnapshot = tag.getSnapshot();

        return TagEvent.newBuilder()
                .setState(DomainEventPublisher.TagEventType.REPLACED.name())
                .setId(tagSnapshot.getId())
                .setTagValue(oldTagValue)
                .setDataBuilder(
                        TagData.newBuilder()
                                .setValue(tagSnapshot.getValue())
                                .setTitle(tagSnapshot.getTitle())
                                .setDescription(tagSnapshot.getDescription())
                                .setOwnerId(tagSnapshot.getOwnerId().toString())
                                .setType(tagSnapshot.getType().name())
                                .setCreateDate(tagSnapshot.getCreateDate().toInstant().toEpochMilli())
                )
                .setOccurredOn(Instant.now().toEpochMilli())
                .build();
    }
}
