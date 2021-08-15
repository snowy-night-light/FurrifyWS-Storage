package ws.furrify.posts.attachment;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.attachment.dto.AttachmentDTO;
import ws.furrify.posts.attachment.dto.AttachmentDtoFactory;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.UUID;

/**
 * @author Skyte
 */
@RequiredArgsConstructor
@Log
public class AttachmentFacade {

    private final CreateAttachment createAttachmentImpl;
    private final DeleteAttachment deleteAttachmentImpl;
    private final UpdateAttachment updateAttachmentImpl;
    private final ReplaceAttachment replaceAttachmentImpl;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentFactory attachmentFactory;
    private final AttachmentDtoFactory attachmentDTOFactory;

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

    /**
     * Replaces all fields in attachment.
     *
     * @param userId        Attachment owner UUID.
     * @param postId        Post UUID
     * @param attachmentId  Attachment UUID
     * @param attachmentDTO Replacement attachment.
     */
    public void replaceAttachment(final UUID userId, final UUID postId, final UUID attachmentId, final AttachmentDTO attachmentDTO) {
        replaceAttachmentImpl.replaceAttachment(userId, postId, attachmentId, attachmentDTO);
    }

    /**
     * Updates specified fields in attachment.
     *
     * @param userId        Attachment owner UUID.
     * @param postId        Post UUID.
     * @param attachmentId  Attachment UUID.
     * @param attachmentDTO Attachment with updated specific fields.
     */
    public void updateAttachment(final UUID userId, final UUID postId, final UUID attachmentId, final AttachmentDTO attachmentDTO) {
        updateAttachmentImpl.updateAttachment(userId, postId, attachmentId, attachmentDTO);
    }

    private void saveAttachmentInDatabase(final AttachmentDTO attachmentDTO) {
        attachmentRepository.save(attachmentFactory.from(attachmentDTO));
    }

    private void deleteAttachmentByAttachmentIdFromDatabase(final UUID attachmentId) {
        attachmentRepository.deleteByAttachmentId(attachmentId);
    }
}
