package ws.furrify.posts.tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import ws.furrify.posts.TagEvent;
import ws.furrify.posts.tag.dto.TagDTO;
import ws.furrify.posts.tag.dto.TagDtoFactory;

import java.util.UUID;

/**
 * @author Skyte
 */
@RequiredArgsConstructor
@Log
public class TagFacade {

    private final CreateTagPort createTagAdapter;
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

        switch (TagEventType.valueOf(tagEvent.getState())) {
            case CREATED -> saveTag(tagDTO);

            default -> log.warning("State received from kafka is not defined. State=" + tagEvent.getState());
        }
    }

    public String createTag(final UUID userId, final TagDTO tagDTO) {
        return createTagAdapter.createTag(userId, tagDTO);
    }

    private void saveTag(final TagDTO tagDTO) {
        tagRepository.save(tagFactory.from(tagDTO));
    }
}
