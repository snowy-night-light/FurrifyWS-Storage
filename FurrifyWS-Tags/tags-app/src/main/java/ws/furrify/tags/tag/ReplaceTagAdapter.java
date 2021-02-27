package ws.furrify.tags.tag;

import lombok.RequiredArgsConstructor;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.tags.tag.dto.TagDTO;
import ws.furrify.tags.tag.vo.TagValue;

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
        tag.updateValue(
                TagValue.of(tagDTO.getValue()),
                tagRepository
        );
        tag.updateType(tagDTO.getType());

        // Publish replace tag event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.TAG,
                // User userId as key
                userId,
                TagUtils.createTagEvent(
                        DomainEventPublisher.TagEventType.REPLACED,
                        tag
                )
        );
    }
}
