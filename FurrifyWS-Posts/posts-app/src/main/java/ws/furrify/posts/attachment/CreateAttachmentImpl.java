package ws.furrify.posts.attachment;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.attachment.dto.AttachmentDTO;
import ws.furrify.posts.attachment.strategy.AttachmentUploadStrategy;
import ws.furrify.posts.post.dto.PostServiceClient;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.time.ZonedDateTime;
import java.util.UUID;

@RequiredArgsConstructor
final class CreateAttachmentImpl implements CreateAttachment {

    private final PostServiceClient postService;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentFactory attachmentFactory;
    private final AttachmentUploadStrategy attachmentUploadStrategy;
    private final DomainEventPublisher<AttachmentEvent> domainEventPublisher;

    @Override
    public UUID createAttachment(@NonNull final UUID userId,
                                 @NonNull final UUID postId,
                                 @NonNull final AttachmentDTO attachmentDTO,
                                 @NonNull final MultipartFile attachmentFile) {
        if (postService.getUserPost(userId, postId) == null) {
            throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(postId.toString()));
        }

        // Generate attachment uuid
        UUID attachmentId = UUID.randomUUID();

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

        // Edit attachmentDTO with generated attachment uuid and attachment file
        AttachmentDTO updatedAttachmentToCreateDTO = attachmentDTO.toBuilder()
                .attachmentId(attachmentId)
                .postId(postId)
                .ownerId(userId)
                .filename(attachmentFile.getOriginalFilename())
                .fileUri(uploadedAttachmentFile.getFileUri())
                .extension(attachmentDTO.getExtension())
                .md5(md5)
                .createDate(ZonedDateTime.now())
                .build();

        // Publish create attachment event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.ATTACHMENT,
                // User userId as key
                userId,
                AttachmentUtils.createAttachmentEvent(
                        DomainEventPublisher.AttachmentEventType.CREATED,
                        attachmentFactory.from(updatedAttachmentToCreateDTO)
                )
        );

        return attachmentId;
    }
}