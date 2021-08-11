package ws.furrify.posts.attachment;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.UUID;

@RequiredArgsConstructor
class DeleteAttachmentAdapter implements DeleteAttachmentPort {

    private final DomainEventPublisher<AttachmentEvent> domainEventPublisher;
    private final AttachmentRepository attachmentRepository;

    @Override
    public void deleteAttachment(@NonNull final UUID userId,
                                 @NonNull final UUID postId,
                                 @NonNull final UUID attachmentId) {
        if (!attachmentRepository.existsByOwnerIdAndPostIdAndAttachmentId(userId, postId, attachmentId)) {
            throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(attachmentId.toString()));
        }

        // Publish delete attachment event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.MEDIA,
                // Use userId as key
                userId,
                AttachmentUtils.deleteAttachmentEvent(postId, attachmentId)
        );
    }
}
