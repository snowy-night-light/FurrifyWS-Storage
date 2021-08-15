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
final class UpdateTagImpl implements UpdateTag {

    private final DomainEventPublisher<TagEvent> domainEventPublisher;
    private final TagRepository tagRepository;

    @Override
    public void updateTag(@NonNull final UUID userId,
                          @NonNull final String value,
                          @NonNull final TagDTO tagDTO) {
        Tag tag = tagRepository.findByOwnerIdAndValue(userId, value)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_TAG_FOUND.getErrorMessage(value)));

        // Update changed fields in tag
        if (tagDTO.getValue() != null) {
            tag.updateValue(
                    TagValue.of(tagDTO.getValue()),
                    tagRepository
            );
        }
        if (tagDTO.getType() != null) {
            tag.updateType(tagDTO.getType());
        }
        if (tagDTO.getTitle() != null) {
            tag.updateDetails(
                    TagTitle.of(tagDTO.getTitle()),
                    TagDescription.of(tag.getSnapshot().getDescription())
            );
        }
        if (tagDTO.getDescription() != null) {
            tag.updateDetails(
                    TagTitle.of(tag.getSnapshot().getTitle()),
                    TagDescription.of(tagDTO.getDescription())
            );
        }

        // Publish update tag event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.TAG,
                // User userId as key
                userId,
                TagUtils.createTagEvent(
                        DomainEventPublisher.TagEventType.UPDATED,
                        tag
                )
        );
    }
}
