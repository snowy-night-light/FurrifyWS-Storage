package ws.furrify.posts.attachment;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ws.furrify.posts.attachment.dto.AttachmentDTO;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.UUID;

@RequiredArgsConstructor
final class UpdateAttachmentImpl implements UpdateAttachment {

    private final DomainEventPublisher<AttachmentEvent> domainEventPublisher;
    private final AttachmentRepository attachmentRepository;

    @Override
    public void updateAttachment(@NonNull final UUID userId,
                                 @NonNull final UUID postId,
                                 @NonNull final UUID attachmentId,
                                 @NonNull final AttachmentDTO attachmentDTO) {
        Attachment attachment = attachmentRepository.findByOwnerIdAndPostIdAndAttachmentId(userId, postId, attachmentId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(attachmentId.toString())));

        // Publish update attachment event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.ATTACHMENT,
                // User userId as key
                userId,
                AttachmentUtils.createAttachmentEvent(
                        DomainEventPublisher.AttachmentEventType.UPDATED,
                        attachment
                )
        );
    }
}
