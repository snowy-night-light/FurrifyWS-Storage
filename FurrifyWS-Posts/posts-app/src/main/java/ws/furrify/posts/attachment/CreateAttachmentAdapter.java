package ws.furrify.posts.attachment;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.attachment.dto.AttachmentDTO;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.FileContentIsCorruptedException;
import ws.furrify.shared.exception.FileExtensionIsNotMatchingContentException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.UUID;

@RequiredArgsConstructor
class CreateAttachmentAdapter implements CreateAttachmentPort {

    private final AttachmentFactory attachmentFactory;
    private final DomainEventPublisher<AttachmentEvent> domainEventPublisher;

    @Override
    public UUID createAttachment(@NonNull final UUID userId,
                                 @NonNull final UUID postId,
                                 @NonNull final AttachmentDTO attachmentDTO,
                                 @NonNull final MultipartFile attachmentFile) {
        // Generate attachment uuid
        UUID attachmentId = UUID.randomUUID();

        // Check if file is matching declared extension
        boolean isFileValid = AttachmentExtension.isValidFile(
                attachmentFile.getOriginalFilename(),
                attachmentFile,
                attachmentDTO.getExtension()
        );

        if (!isFileValid) {
            throw new FileExtensionIsNotMatchingContentException(Errors.FILE_EXTENSION_IS_NOT_MATCHING_CONTENT.getErrorMessage());
        }

        String md5;
        try {
            // Get file hash
            md5 = DigestUtils.md5Hex(attachmentFile.getInputStream());
        } catch (IOException e) {
            throw new FileContentIsCorruptedException(Errors.FILE_CONTENT_IS_CORRUPTED.getErrorMessage());
        }

        // TODO Upload file to processing server

        // Edit attachmentDTO with generated attachment uuid
        AttachmentDTO updatedAttachmentToCreateDTO = attachmentDTO.toBuilder()
                .attachmentId(attachmentId)
                .postId(postId)
                .ownerId(userId)
                .filename(attachmentFile.getOriginalFilename())
                .md5(md5)
                .createDate(ZonedDateTime.now())
                .build();

        // Publish create attachment event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.MEDIA,
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