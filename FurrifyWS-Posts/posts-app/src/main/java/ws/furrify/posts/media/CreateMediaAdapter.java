package ws.furrify.posts.media;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.media.dto.MediaDTO;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.FileContentIsCorruptedException;
import ws.furrify.shared.exception.FileExtensionIsNotMatchingContentException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.UUID;

@RequiredArgsConstructor
class CreateMediaAdapter implements CreateMediaPort {

    private final MediaFactory mediaFactory;
    private final DomainEventPublisher<MediaEvent> domainEventPublisher;

    @Override
    public UUID createMedia(@NonNull final UUID userId,
                            @NonNull final UUID postId,
                            @NonNull final MediaDTO mediaDTO,
                            @NonNull final MultipartFile mediaFile) {
        // Generate media uuid
        UUID mediaId = UUID.randomUUID();

        // Check if file is matching declared extension
        boolean isFileValid = MediaExtension.isValidFile(
                mediaFile.getOriginalFilename(),
                mediaFile,
                mediaDTO.getExtension()
        );

        if (!isFileValid) {
            throw new FileExtensionIsNotMatchingContentException(Errors.FILE_EXTENSION_IS_NOT_MATCHING_CONTENT.getErrorMessage());
        }

        String md5;
        try {
            // Get file hash
            md5 = DigestUtils.md5Hex(mediaFile.getInputStream());
        } catch (IOException e) {
            throw new FileContentIsCorruptedException(Errors.FILE_CONTENT_IS_CORRUPTED.getErrorMessage());
        }

        // TODO Upload file to processing server

        // Edit mediaDTO with generated media uuid
        MediaDTO updatedMediaToCreateDTO = mediaDTO.toBuilder()
                .mediaId(mediaId)
                .postId(postId)
                .ownerId(userId)
                .filename(mediaFile.getOriginalFilename())
                .md5(md5)
                .priority(mediaDTO.getPriority())
                .createDate(ZonedDateTime.now())
                .build();

        // Publish create media event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.MEDIA,
                // User userId as key
                userId,
                MediaUtils.createMediaEvent(
                        DomainEventPublisher.MediaEventType.CREATED,
                        mediaFactory.from(updatedMediaToCreateDTO)
                )
        );

        return mediaId;
    }
}