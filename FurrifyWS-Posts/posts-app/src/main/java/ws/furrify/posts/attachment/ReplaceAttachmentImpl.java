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
final class ReplaceAttachmentImpl implements ReplaceAttachment {

    private final DomainEventPublisher<AttachmentEvent> domainEventPublisher;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentUploadStrategy attachmentUploadStrategy;

    @Override
    public void replaceAttachment(@NonNull final UUID userId,
                                  @NonNull final UUID postId,
                                  @NonNull final UUID attachmentId,
                                  @NonNull final AttachmentDTO attachmentDTO,
                                  @NonNull final MultipartFile attachmentFile) {
        Attachment attachment = attachmentRepository.findByOwnerIdAndPostIdAndAttachmentId(userId, postId, attachmentId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(attachmentId.toString())));

        // Replace fields in attachment

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

        // Upload attachment file
        AttachmentUploadStrategy.UploadedAttachmentFile uploadedAttachmentFile = attachmentUploadStrategy.uploadAttachment(
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

        // Publish update attachment event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.ATTACHMENT,
                // User userId as key
                userId,
                AttachmentUtils.createAttachmentEvent(
                        DomainEventPublisher.AttachmentEventType.REPLACED,
                        attachment
                )
        );
    }


}
