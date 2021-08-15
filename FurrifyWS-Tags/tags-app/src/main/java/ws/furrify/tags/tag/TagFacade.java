package ws.furrify.tags.tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.tags.tag.dto.TagDTO;
import ws.furrify.tags.tag.dto.TagDtoFactory;

import java.util.UUID;

/**
 * @author Skyte
 */
@RequiredArgsConstructor
@Log
public class TagFacade {

    private final CreateTag createTagImpl;
    private final DeleteTag deleteTagImpl;
    private final UpdateTag updateTagImpl;
    private final ReplaceTag replaceTagImpl;
    private final TagRepository tagRepository;
    private final TagFactory tagFactory;
    private final TagDtoFactory tagDtoFactory;

    /**
     * Handle incoming events.
     *
     * @param tagEvent Tag event instance received from kafka.
     */
    public void handleEvent(final UUID key, final TagEvent tagEvent) {
        TagDTO tagDTO = tagDtoFactory.from(key, tagEvent);

        switch (DomainEventPublisher.TagEventType.valueOf(tagEvent.getState())) {
            case CREATED, REPLACED, UPDATED -> saveTag(tagDTO);

            case REMOVED -> deleteTagByValue(tagDTO.getValue());

            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + tagEvent.getState() + " Topic=tag_events");
        }
    }

    /**
     * Creates tag.
     *
     * @param userId User uuid to assign tag to.
     * @param tagDTO Tag to create.
     * @return Created tag UUID.
     */
    public String createTag(final UUID userId, final TagDTO tagDTO) {
        return createTagImpl.createTag(userId, tagDTO);
    }

    /**
     * Deletes tag.
     *
     * @param value Tag unique value.
     */
    public void deleteTag(final UUID userId, final String value) {
        deleteTagImpl.deleteTag(userId, value);
    }

    /**
     * Replaces all fields in tag.
     *
     * @param value  Tag unique value.
     * @param tagDTO Replacement tag.
     */
    public void replaceTag(final UUID userId, final String value, final TagDTO tagDTO) {
        replaceTagImpl.replaceTag(userId, value, tagDTO);
    }

    /**
     * Updates specified fields in tag.
     *
     * @param value  Tag unique value.
     * @param tagDTO Tag with updated specific fields.
     */
    public void updateTag(final UUID userId, final String value, final TagDTO tagDTO) {
        updateTagImpl.updateTag(userId, value, tagDTO);
    }

    private void saveTag(final TagDTO tagDTO) {
        tagRepository.save(tagFactory.from(tagDTO));
    }

    private void deleteTagByValue(final String value) {
        tagRepository.deleteByValue(value);
    }
}
