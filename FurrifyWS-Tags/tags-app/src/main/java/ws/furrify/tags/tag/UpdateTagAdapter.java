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
class UpdateTagAdapter implements UpdateTagPort {

    private final DomainEventPublisher<TagEvent> domainEventPublisher;
    private final TagRepository tagRepository;

    @Override
    public void updateTag(final UUID userId, final String value, final TagDTO tagDTO) {
        Tag tag = tagRepository.findByOwnerIdAndValue(userId, value)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_TAG_FOUND.getErrorMessage(value)));

        // Update changed fields in tag
        if (tagDTO.getValue() != null) {
            tag.updateValue(tagDTO.getValue(), tagRepository);
        }
        if (tagDTO.getType() != null) {
            tag.updateType(tagDTO.getType());
        }
        if (tagDTO.getTitle() != null) {
            tag.updateDetails(tagDTO.getTitle(), tag.getSnapshot().getDescription());
        }
        if (tagDTO.getDescription() != null) {
            tag.updateDetails(tag.getSnapshot().getTitle(), tagDTO.getDescription());
        }

        // Publish update tag event
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
                .setState(DomainEventPublisher.TagEventType.UPDATED.name())
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
