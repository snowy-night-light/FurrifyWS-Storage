package ws.furrify.posts.media;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ws.furrify.posts.artist.ArtistServiceClient;
import ws.furrify.posts.media.dto.MediaDTO;
import ws.furrify.posts.media.vo.MediaPriority;
import ws.furrify.posts.tag.TagServiceClient;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.UUID;

@RequiredArgsConstructor
class ReplaceMediaAdapter implements ReplaceMediaPort {

    private final DomainEventPublisher<MediaEvent> domainEventPublisher;
    private final MediaRepository mediaRepository;
    private final TagServiceClient tagServiceClient;
    private final ArtistServiceClient artistServiceClient;

    @Override
    public void replaceMedia(@NonNull final UUID userId,
                             @NonNull final UUID postId,
                             @NonNull final UUID mediaId,
                             @NonNull final MediaDTO mediaDTO) {
        Media media = mediaRepository.findByOwnerIdAndPostIdAndMediaId(userId, postId, mediaId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(mediaId.toString())));

        // Update priority in media
        media.replacePriority(
                MediaPriority.of(mediaDTO.getPriority())
        );

        // Publish replace media details event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.MEDIA,
                // User userId as key
                userId,
                MediaUtils.createMediaEvent(
                        DomainEventPublisher.MediaEventType.REPLACED,
                        media
                )
        );
    }
}
