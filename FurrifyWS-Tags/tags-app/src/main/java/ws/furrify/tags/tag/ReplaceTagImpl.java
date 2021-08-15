package ws.furrify.tags.tag;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.tags.tag.dto.TagDTO;
import ws.furrify.tags.tag.vo.TagDescription;
import ws.furrify.tags.tag.vo.TagTitle;
import ws.furrify.tags.tag.vo.TagValue;

import java.util.UUID;

@RequiredArgsConstructor
final class ReplaceTagImpl implements ReplaceTag {

    private final DomainEventPublisher<TagEvent> domainEventPublisher;
    private final TagRepository tagRepository;

    @Override
    public void replaceTag(@NonNull final UUID userId,
                           @NonNull final String value,
                           @NonNull final TagDTO tagDTO) {
        Tag tag = tagRepository.findByOwnerIdAndValue(userId, value)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_TAG_FOUND.getErrorMessage(value)));

        // Update fields in tag
        tag.updateTitle(
                TagTitle.of(tagDTO.getTitle())
        );
        tag.updateDescription(
                TagDescription.of(tagDTO.getDescription())
        );
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
