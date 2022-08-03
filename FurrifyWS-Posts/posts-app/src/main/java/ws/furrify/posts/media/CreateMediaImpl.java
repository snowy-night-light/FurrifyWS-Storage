package ws.furrify.posts.media;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.media.dto.MediaDTO;
import ws.furrify.posts.media.strategy.MediaUploadStrategy;
import ws.furrify.posts.post.dto.PostServiceClient;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
final class CreateMediaImpl implements CreateMedia {

    private final PostServiceClient postService;
    private final MediaRepository mediaRepository;
    private final MediaFactory mediaFactory;
    private final MediaUploadStrategy mediaUploadStrategy;
    private final DomainEventPublisher<MediaEvent> domainEventPublisher;

    @Override
    public UUID createMedia(@NonNull final UUID userId,
                            @NonNull final UUID postId,
                            @NonNull final MediaDTO mediaDTO,
                            @NonNull final MultipartFile mediaFile,
                            final MultipartFile thumbnailFile) {
        if (postService.getUserPost(userId, postId) == null) {
            throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(postId.toString()));
        }

        // Generate media uuid
        UUID mediaId = UUID.randomUUID();

        final String md5 = MediaFileUtils.generateMd5FromFile(mediaFile);

        // Validate file
        MediaFileUtils.validateMedia(
                userId,
                postId,
                mediaDTO,
                mediaFile,
                md5,
                mediaRepository
        );

        MediaUploadStrategy.UploadedMediaFile uploadedMediaFile;

        // If thumbnail file is present
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {

            // Validate file
            MediaFileUtils.validateThumbnail(
                    thumbnailFile
            );

            // Upload media with thumbnail
            uploadedMediaFile = mediaUploadStrategy.uploadMedia(
                    mediaId,
                    mediaDTO.getExtension(),
                    mediaFile,
                    thumbnailFile
            );
        } else {
            // Upload media with thumbnail
            uploadedMediaFile = mediaUploadStrategy.uploadMediaWithGeneratedThumbnail(
                    mediaId,
                    mediaDTO.getExtension(),
                    mediaFile
            );
        }

        // Edit mediaDTO with generated media uuid and attachment file
        MediaDTO updatedMediaToCreateDTO = mediaDTO.toBuilder()
                .mediaId(mediaId)
                .postId(postId)
                .ownerId(userId)
                .filename(Objects.requireNonNull(mediaFile.getOriginalFilename()))
                .fileUri(uploadedMediaFile.getFileUri())
                .thumbnailUri(uploadedMediaFile.getThumbnailUri())
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