package ws.furrify.posts.media;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ws.furrify.posts.media.dto.MediaDTO;
import ws.furrify.posts.media.vo.MediaPriority;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.UUID;

@RequiredArgsConstructor
class UpdateMediaAdapter implements UpdateMediaPort {

    private final DomainEventPublisher<MediaEvent> domainEventPublisher;
    private final MediaRepository mediaRepository;

    @Override
    public void updateMedia(@NonNull final UUID userId,
                            @NonNull final UUID postId,
                            @NonNull final UUID mediaId,
                            @NonNull final MediaDTO mediaDTO) {
        Media media = mediaRepository.findByOwnerIdAndPostIdAndMediaId(userId, postId, mediaId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(mediaDTO.toString())));

        // Update changed fields in media
        if (mediaDTO.getPriority() != null) {
            media.replacePriority(
                    MediaPriority.of(mediaDTO.getPriority())
            );
        }

        // Publish update media event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.MEDIA,
                // User userId as key
                userId,
                MediaUtils.createMediaEvent(
                        DomainEventPublisher.MediaEventType.UPDATED,
                        media
                )
        );
    }
}
