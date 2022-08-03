package ws.furrify.posts.media;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ws.furrify.posts.media.strategy.MediaUploadStrategy;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.UUID;

@RequiredArgsConstructor
final class DeleteMediaImpl implements DeleteMedia {

    private final DomainEventPublisher<MediaEvent> domainEventPublisher;
    private final MediaRepository mediaRepository;

    private final MediaUploadStrategy mediaUploadStrategy;

    @Override
    public void deleteMedia(@NonNull final UUID userId,
                            @NonNull final UUID postId,
                            @NonNull final UUID mediaId) {
        if (!mediaRepository.existsByOwnerIdAndPostIdAndMediaId(userId, postId, mediaId)) {
            throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(mediaId.toString()));
        }

        mediaUploadStrategy.removeAllMediaFiles(mediaId);

        // Publish delete media event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.MEDIA,
                // Use userId as key
                userId,
                MediaUtils.deleteMediaEvent(postId, mediaId)
        );
    }
}
