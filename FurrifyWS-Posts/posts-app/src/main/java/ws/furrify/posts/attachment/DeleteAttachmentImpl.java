package ws.furrify.posts.attachment;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ws.furrify.posts.attachment.strategy.AttachmentUploadStrategy;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.UUID;

@RequiredArgsConstructor
final class DeleteAttachmentImpl implements DeleteAttachment {

    private final DomainEventPublisher<AttachmentEvent> domainEventPublisher;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentUploadStrategy attachmentUploadStrategy;

    @Override
    public void deleteAttachment(@NonNull final UUID userId,
                                 @NonNull final UUID postId,
                                 @NonNull final UUID attachmentId) {
        if (!attachmentRepository.existsByOwnerIdAndPostIdAndAttachmentId(userId, postId, attachmentId)) {
            throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(attachmentId.toString()));
        }

        attachmentUploadStrategy.removeAllAttachmentFiles(attachmentId);

        // Publish delete attachment event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.ATTACHMENT,
                // Use userId as key
                userId,
                AttachmentUtils.deleteAttachmentEvent(postId, attachmentId)
        );
    }
}
