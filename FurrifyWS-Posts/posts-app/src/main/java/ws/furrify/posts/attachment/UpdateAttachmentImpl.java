package ws.furrify.posts.attachment;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.attachment.dto.AttachmentDTO;
import ws.furrify.posts.attachment.strategy.AttachmentUploadStrategy;
import ws.furrify.posts.attachment.vo.AttachmentFile;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
final class UpdateAttachmentImpl implements UpdateAttachment {

    private final DomainEventPublisher<AttachmentEvent> domainEventPublisher;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentUploadStrategy attachmentUploadStrategy;

    @Override
    public void updateAttachment(@NonNull final UUID userId,
                                 @NonNull final UUID postId,
                                 @NonNull final UUID attachmentId,
                                 @NonNull final AttachmentDTO attachmentDTO,
                                 final MultipartFile attachmentFile) {
        Attachment attachment = attachmentRepository.findByOwnerIdAndPostIdAndAttachmentId(userId, postId, attachmentId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(attachmentId.toString())));

        // Update changed fields in attachment

        // If new attachment file provided with extension
        if (attachmentFile != null && !attachmentFile.isEmpty()) {
            final String md5 = AttachmentFileUtils.generateMd5FromFile(attachmentFile);

            // Validate file
            AttachmentFileUtils.validateAttachment(
                    userId,
                    postId,
                    attachmentDTO,
                    attachmentFile,
                    md5,
                    attachmentRepository
            );

            AttachmentUploadStrategy.UploadedAttachmentFile uploadedAttachmentFile;

            // Upload attachment file
            uploadedAttachmentFile = attachmentUploadStrategy.uploadAttachment(
                    attachmentId,
                    attachmentFile
            );

            attachment.replaceAttachmentFile(
                    AttachmentFile.builder()
                            .filename(Objects.requireNonNull(attachmentFile.getOriginalFilename()))
                            .fileUri(uploadedAttachmentFile.getFileUri())
                            .extension(attachmentDTO.getExtension())
                            .md5(md5)
                            .build()
            );
        }

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
