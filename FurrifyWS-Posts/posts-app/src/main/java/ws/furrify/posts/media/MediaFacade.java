package ws.furrify.posts.media;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.media.dto.MediaDTO;
import ws.furrify.posts.media.dto.MediaDtoFactory;
import ws.furrify.posts.media.strategy.MediaUploadStrategy;
import ws.furrify.posts.media.vo.MediaSource;
import ws.furrify.posts.post.PostEvent;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.shared.vo.SourceOriginType;
import ws.furrify.sources.source.SourceEvent;

import java.util.Set;
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

    private final MediaUploadStrategy mediaUploadStrategy;

    /**
     * Handle incoming post events.
     *
     * @param postEvent Post event instance received from kafka.
     */
    public void handleEvent(final UUID key, final PostEvent postEvent) {
        switch (DomainEventPublisher.PostEventType.valueOf(postEvent.getState())) {
            case REMOVED -> deleteMediaByPostIdAndRemoveMediaFiles(key, UUID.fromString(postEvent.getPostId()));

            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + postEvent.getState() + " Topic=post_events");
        }
    }

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
     * Handle incoming source events.
     *
     * @param sourceEvent Source event instance received from kafka.
     */
    @SneakyThrows
    public void handleEvent(final UUID key, final SourceEvent sourceEvent) {
        UUID sourceId = UUID.fromString(sourceEvent.getSourceId());

        // Check if this source event origins from Media
        if (!SourceOriginType.MEDIA.name().equals(sourceEvent.getData().getOriginType())) {
            return;
        }

        switch (DomainEventPublisher.SourceEventType.valueOf(sourceEvent.getState())) {
            case REMOVED -> deleteSourceFromMedia(
                    key,
                    UUID.fromString(sourceEvent.getData().getPostId()),
                    UUID.fromString(sourceEvent.getData().getOriginId()),
                    sourceId
            );
            case UPDATED, REPLACED -> updateSourceDataInMedia(
                    key,
                    UUID.fromString(sourceEvent.getData().getPostId()),
                    UUID.fromString(sourceEvent.getData().getOriginId()),
                    // Build post source from source event
                    MediaSource.builder()
                            .sourceId(sourceId)
                            .strategy(sourceEvent.getData().getStrategy())
                            .data(sourceEvent.getData().getDataHashMap())
                            .build()
            );
            case CREATED -> addSourceToMedia(
                    key,
                    UUID.fromString(sourceEvent.getData().getPostId()),
                    UUID.fromString(sourceEvent.getData().getOriginId()),
                    // Build post source from source event
                    MediaSource.builder()
                            .sourceId(sourceId)
                            .strategy(sourceEvent.getData().getStrategy())
                            .data(sourceEvent.getData().getDataHashMap())
                            .build()
            );
            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + sourceEvent.getState() + " Topic=source_events");
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
                            final MultipartFile mediaFile,
                            final MultipartFile thumbnailFile) {
        return createMediaImpl.createMedia(userId, postId, mediaDTO, mediaFile, thumbnailFile);
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
     * Replaces all fields in media and updates files.
     *
     * @param userId   Media owner UUID.
     * @param postId   Post UUID
     * @param mediaId  Media UUID
     * @param mediaDTO Replacement media.
     */
    public void replaceMedia(final UUID userId,
                             final UUID postId,
                             final UUID mediaId,
                             final MediaDTO mediaDTO,
                             final MultipartFile mediaFile,
                             final MultipartFile thumbnailFile) {
        replaceMediaImpl.replaceMedia(userId, postId, mediaId, mediaDTO, mediaFile, thumbnailFile);
    }

    /**
     * Updates specified fields in media and if specified also files.
     *
     * @param userId   Media owner UUID.
     * @param postId   Post UUID.
     * @param mediaId  Media UUID.
     * @param mediaDTO Media with updated specific fields.
     */
    public void updateMedia(final UUID userId,
                            final UUID postId,
                            final UUID mediaId,
                            final MediaDTO mediaDTO,
                            final MultipartFile mediaFile,
                            final MultipartFile thumbnailFile) {
        updateMediaImpl.updateMedia(userId, postId, mediaId, mediaDTO, mediaFile, thumbnailFile);
    }

    private void saveMediaInDatabase(final MediaDTO mediaDTO) {
        mediaRepository.save(mediaFactory.from(mediaDTO));
    }

    private void deleteMediaByPostIdAndRemoveMediaFiles(final UUID ownerId, final UUID postId) {
        Set<Media> mediaSet = mediaRepository.findAllByOwnerIdAndPostId(ownerId, postId);
        mediaSet.forEach(media -> {
            MediaSnapshot snapshot = media.getSnapshot();

            mediaUploadStrategy.removeMediaFiles(snapshot.getMediaId());

            mediaRepository.deleteByMediaId(snapshot.getMediaId());
        });
    }

    private void deleteMediaByMediaIdFromDatabase(final UUID mediaId) {
        mediaRepository.deleteByMediaId(mediaId);
    }

    private void deleteSourceFromMedia(final UUID ownerId,
                                       final UUID postId,
                                       final UUID mediaId,
                                       final UUID sourceId) {
        Media media = mediaRepository.findByOwnerIdAndPostIdAndMediaId(ownerId, postId, mediaId)
                .orElseThrow(() -> new IllegalStateException(Errors.NO_RECORD_FOUND.getErrorMessage(mediaId)));
        media.deleteSource(sourceId);

        mediaRepository.save(media);
    }

    private void updateSourceDataInMedia(final UUID ownerId,
                                         final UUID postId,
                                         final UUID mediaId,
                                         final MediaSource mediaSource) {
        Media media = mediaRepository.findByOwnerIdAndPostIdAndMediaId(ownerId, postId, mediaId)
                .orElseThrow(() -> new IllegalStateException(Errors.NO_RECORD_FOUND.getErrorMessage(mediaId)));
        media.updateSourceDataInSources(mediaSource);

        mediaRepository.save(media);
    }

    private void addSourceToMedia(final UUID ownerId,
                                  final UUID postId,
                                  final UUID mediaId,
                                  final MediaSource mediaSource) {
        Media media = mediaRepository.findByOwnerIdAndPostIdAndMediaId(ownerId, postId, mediaId)
                .orElseThrow(() -> new IllegalStateException(Errors.NO_RECORD_FOUND.getErrorMessage(mediaId)));
        media.addSource(mediaSource);

        mediaRepository.save(media);
    }

}
