package ws.furrify.posts.media;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import ws.furrify.posts.media.dto.MediaDTO;
import ws.furrify.posts.media.dto.MediaDtoFactory;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.UUID;

/**
 * @author Skyte
 */
@RequiredArgsConstructor
@Log
public class MediaFacade {

    private final CreateMediaPort createMediaAdapter;
    private final DeleteMediaPort deleteMediaAdapter;
    private final UpdateMediaPort updateMediaAdapter;
    private final ReplaceMediaPort replaceMediaAdapter;
    private final MediaRepository mediaRepository;
    private final MediaFactory mediaFactory;
    private final MediaDtoFactory mediaDTOFactory;

    /**
     * Handle incoming media events.
     *
     * @param mediaEvent Media event instance received from kafka.
     */
    void handleEvent(final UUID key, final MediaEvent mediaEvent) {
        MediaDTO mediaDTO = mediaDTOFactory.from(key, mediaEvent);

        switch (DomainEventPublisher.MediaEventType.valueOf(mediaEvent.getState())) {
            case CREATED, REPLACED, UPDATED -> saveMedia(mediaDTO);
            case REMOVED -> deleteMediaByMediaId(mediaDTO.getMediaId());

            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + mediaEvent.getState() + " Topic=media_events");
        }
    }

    /**
     * Creates media.
     *
     * @param userId   User uuid to assign media to.
     * @param postId   Post uuid to assign media to.
     * @param mediaDTO Post to create.
     * @return Created media UUID.
     */
    public UUID createMedia(final UUID userId, final UUID postId, final MediaDTO mediaDTO) {
        return createMediaAdapter.createMedia(userId, postId, mediaDTO);
    }

    /**
     * Deletes media.
     *
     * @param userId  Media owner UUID.
     * @param postId  Post UUID.
     * @param mediaId Media UUID.
     */
    public void deleteMedia(final UUID userId, final UUID postId, final UUID mediaId) {
        deleteMediaAdapter.deleteMedia(userId, postId, mediaId);
    }

    /**
     * Replaces all fields in media.
     *
     * @param userId   Media owner UUID.
     * @param postId   Post UUID
     * @param mediaId  Media UUID
     * @param mediaDTO Replacement media.
     */
    public void replaceMedia(final UUID userId, final UUID postId, final UUID mediaId, final MediaDTO mediaDTO) {
        replaceMediaAdapter.replaceMedia(userId, postId, mediaId, mediaDTO);
    }

    /**
     * Updates specified fields in media.
     *
     * @param userId   Media owner UUID.
     * @param postId   Post UUID.
     * @param mediaId  Media UUID.
     * @param mediaDTO Media with updated specific fields.
     */
    public void updateMedia(final UUID userId, final UUID postId, final UUID mediaId, final MediaDTO mediaDTO) {
        updateMediaAdapter.updateMedia(userId, postId, mediaId, mediaDTO);
    }

    private void saveMedia(final MediaDTO mediaDTO) {
        mediaRepository.save(mediaFactory.from(mediaDTO));
    }

    private void deleteMediaByMediaId(final UUID mediaId) {
        mediaRepository.deleteByMediaId(mediaId);
    }
}
