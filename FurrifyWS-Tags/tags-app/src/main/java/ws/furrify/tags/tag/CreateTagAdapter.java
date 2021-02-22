package ws.furrify.tags.tag;

import lombok.RequiredArgsConstructor;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordAlreadyExistsException;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.tags.tag.dto.TagDTO;

import java.time.ZonedDateTime;
import java.util.UUID;

@RequiredArgsConstructor
class CreateTagAdapter implements CreateTagPort {

    private final TagFactory tagFactory;
    private final DomainEventPublisher<TagEvent> domainEventPublisher;
    private final TagRepository tagRepository;

    @Override
    public String createTag(final UUID userId, final TagDTO tagDTO) {
        if (tagRepository.existsByOwnerIdAndValue(userId, tagDTO.getValue())) {
            throw new RecordAlreadyExistsException(Errors.TAG_ALREADY_EXISTS.getErrorMessage(tagDTO.getValue()));
        }

        // Edit tagDTO with current time and userId
        TagDTO updatedTagToCreateDTO = tagDTO.toBuilder()
                .ownerId(userId)
                .createDate(ZonedDateTime.now())
                .build();

        // Publish create tag event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.TAG,
                // User userId as key
                userId,
                TagUtils.createTagEvent(
                        DomainEventPublisher.TagEventType.CREATED,
                        tagFactory.from(updatedTagToCreateDTO)
                )
        );

        return updatedTagToCreateDTO.getValue();
    }
}
