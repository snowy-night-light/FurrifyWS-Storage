package ws.furrify.posts.media;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.media.dto.MediaDTO;
import ws.furrify.posts.media.dto.MediaDtoFactory;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.UUID;

/**
 * @author Skyte
 */
@RequiredArgsConstructor
@Log
final public class MediaFacade {

    private final CreateMedia createMediaImpl;
    private final DeleteMedia deleteMediaImpl;
    private final UpdateMedia updateMediaImpl;
    private final ReplaceMedia replaceMediaImpl;
    private final MediaRepository mediaRepository;
    private final MediaFactory mediaFactory;
    private final MediaDtoFactory mediaDTOFactory;

    /**
     * Handle incoming media events.
     *
     * @param mediaEvent Media event instance received from kafka.
     */
    public void handleEvent(final UUID key, final MediaEvent mediaEvent) {
        MediaDTO mediaDTO = mediaDTOFactory.from(key, mediaEvent);

        switch (DomainEventPublisher.MediaEventType.valueOf(mediaEvent.getState())) {
            case CREATED, REPLACED, UPDATED -> saveMediaInDatabase(mediaDTO);
            case REMOVED -> deleteMediaByMediaIdFromDatabase(mediaDTO.getMediaId());

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
    public UUID createMedia(final UUID userId,
                            final UUID postId,
                            final MediaDTO mediaDTO,
                            final MultipartFile mediaFile) {
        return createMediaImpl.createMedia(userId, postId, mediaDTO, mediaFile);
    }

    /**
     * Deletes media.
     *
     * @param userId  Media owner UUID.
     * @param postId  Post UUID.
     * @param mediaId Media UUID.
     */
    public void deleteMedia(final UUID userId, final UUID postId, final UUID mediaId) {
        deleteMediaImpl.deleteMedia(userId, postId, mediaId);
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
        replaceMediaImpl.replaceMedia(userId, postId, mediaId, mediaDTO);
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
        updateMediaImpl.updateMedia(userId, postId, mediaId, mediaDTO);
    }

    private void saveMediaInDatabase(final MediaDTO mediaDTO) {
        mediaRepository.save(mediaFactory.from(mediaDTO));
    }

    private void deleteMediaByMediaIdFromDatabase(final UUID mediaId) {
        mediaRepository.deleteByMediaId(mediaId);
    }
}
