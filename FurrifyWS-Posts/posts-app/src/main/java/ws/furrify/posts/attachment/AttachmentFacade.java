package ws.furrify.posts.attachment;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.attachment.dto.AttachmentDTO;
import ws.furrify.posts.attachment.dto.AttachmentDtoFactory;
import ws.furrify.posts.attachment.strategy.AttachmentUploadStrategy;
import ws.furrify.posts.attachment.vo.AttachmentSource;
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
public class AttachmentFacade {

    private final CreateAttachment createAttachmentImpl;
    private final DeleteAttachment deleteAttachmentImpl;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentFactory attachmentFactory;
    private final AttachmentDtoFactory attachmentDTOFactory;
    private final AttachmentUploadStrategy attachmentUploadStrategy;

    /**
     * Handle incoming post events.
     *
     * @param postEvent Post event instance received from kafka.
     */
    public void handleEvent(final UUID key, final PostEvent postEvent) {
        switch (DomainEventPublisher.PostEventType.valueOf(postEvent.getState())) {
            case REMOVED ->
                    deleteAttachmentByPostIdAndRemoveAttachmentFiles(key, UUID.fromString(postEvent.getPostId()));

            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + postEvent.getState() + " Topic=post_events");
        }
    }

    /**
     * Handle incoming attachment events.
     *
     * @param attachmentEvent Attachment event instance received from kafka.
     */
    public void handleEvent(final UUID key, final AttachmentEvent attachmentEvent) {
        AttachmentDTO attachmentDTO = attachmentDTOFactory.from(key, attachmentEvent);

        switch (DomainEventPublisher.AttachmentEventType.valueOf(attachmentEvent.getState())) {
            case CREATED, REPLACED, UPDATED -> saveAttachmentInDatabase(attachmentDTO);
            case REMOVED -> deleteAttachmentByAttachmentIdFromDatabase(attachmentDTO.getAttachmentId());

            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + attachmentEvent.getState() + " Topic=attachment_events");
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

        // Check if this source event origins from Attachment
        if (!SourceOriginType.ATTACHMENT.name().equals(sourceEvent.getData().getOriginType())) {
            return;
        }

        switch (DomainEventPublisher.SourceEventType.valueOf(sourceEvent.getState())) {
            case REMOVED -> deleteSourceFromAttachment(
                    key,
                    UUID.fromString(sourceEvent.getData().getPostId()),
                    UUID.fromString(sourceEvent.getData().getOriginId()),
                    sourceId
            );
            case UPDATED, REPLACED -> updateSourceDataInAttachment(
                    key,
                    UUID.fromString(sourceEvent.getData().getPostId()),
                    UUID.fromString(sourceEvent.getData().getOriginId()),
                    // Build post source from source event
                    AttachmentSource.builder()
                            .sourceId(sourceId)
                            .strategy(sourceEvent.getData().getStrategy())
                            .data(sourceEvent.getData().getDataHashMap())
                            .build()
            );
            case CREATED -> addSourceToAttachment(
                    key,
                    UUID.fromString(sourceEvent.getData().getPostId()),
                    UUID.fromString(sourceEvent.getData().getOriginId()),
                    // Build post source from source event
                    AttachmentSource.builder()
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
     * Creates attachment.
     *
     * @param userId        User uuid to assign attachment to.
     * @param postId        Post uuid to assign attachment to.
     * @param attachmentDTO Post to create.
     * @return Created attachment UUID.
     */
    public UUID createAttachment(final UUID userId,
                                 final UUID postId,
                                 final AttachmentDTO attachmentDTO,
                                 final MultipartFile attachmentFile) {
        return createAttachmentImpl.createAttachment(userId, postId, attachmentDTO, attachmentFile);
    }

    /**
     * Deletes attachment.
     *
     * @param userId       Attachment owner UUID.
     * @param postId       Post UUID.
     * @param attachmentId Attachment UUID.
     */
    public void deleteAttachment(final UUID userId, final UUID postId, final UUID attachmentId) {
        deleteAttachmentImpl.deleteAttachment(userId, postId, attachmentId);
    }

    private void deleteAttachmentByPostIdAndRemoveAttachmentFiles(final UUID ownerId, final UUID postId) {
        Set<Attachment> attachments = attachmentRepository.findAllByOwnerIdAndPostId(ownerId, postId);
        attachments.forEach(attachment -> {
            AttachmentSnapshot snapshot = attachment.getSnapshot();

            attachmentUploadStrategy.removeAttachmentFiles(snapshot.getAttachmentId());

            attachmentRepository.deleteByAttachmentId(snapshot.getAttachmentId());
        });
    }

    private void saveAttachmentInDatabase(final AttachmentDTO attachmentDTO) {
        attachmentRepository.save(attachmentFactory.from(attachmentDTO));
    }

    private void deleteAttachmentByAttachmentIdFromDatabase(final UUID attachmentId) {
        attachmentRepository.deleteByAttachmentId(attachmentId);
    }

    private void deleteSourceFromAttachment(final UUID ownerId,
                                            final UUID postId,
                                            final UUID attachmentId,
                                            final UUID sourceId) {
        Attachment attachment = attachmentRepository.findByOwnerIdAndPostIdAndAttachmentId(ownerId, postId, attachmentId)
                .orElseThrow(() -> new IllegalStateException(Errors.NO_RECORD_FOUND.getErrorMessage(attachmentId)));
        attachment.deleteSource(sourceId);

        attachmentRepository.save(attachment);
    }

    private void updateSourceDataInAttachment(final UUID ownerId,
                                              final UUID postId,
                                              final UUID attachmentId,
                                              final AttachmentSource attachmentSource) {
        Attachment attachment = attachmentRepository.findByOwnerIdAndPostIdAndAttachmentId(ownerId, postId, attachmentId)
                .orElseThrow(() -> new IllegalStateException(Errors.NO_RECORD_FOUND.getErrorMessage(attachmentId)));
        attachment.updateSourceDataInSources(attachmentSource);

        attachmentRepository.save(attachment);
    }

    private void addSourceToAttachment(final UUID ownerId,
                                       final UUID postId,
                                       final UUID attachmentId,
                                       final AttachmentSource attachmentSource) {
        Attachment attachment = attachmentRepository.findByOwnerIdAndPostIdAndAttachmentId(ownerId, postId, attachmentId)
                .orElseThrow(() -> new IllegalStateException(Errors.NO_RECORD_FOUND.getErrorMessage(attachmentId)));
        attachment.addSource(attachmentSource);

        attachmentRepository.save(attachment);
    }
}
