package ws.furrify.posts.media;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ws.furrify.posts.media.dto.MediaDTO;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.time.ZonedDateTime;
import java.util.UUID;

@RequiredArgsConstructor
class CreateMediaAdapter implements CreateMediaPort {

    private final MediaFactory mediaFactory;
    private final DomainEventPublisher<MediaEvent> domainEventPublisher;

    @Override
    public UUID createMedia(@NonNull final UUID userId,
                            @NonNull final UUID postId,
                            @NonNull final MediaDTO mediaDTO) {
        // Generate media uuid
        UUID mediaId = UUID.randomUUID();

        // Edit mediaDTO with generated media uuid
        MediaDTO updatedMediaToCreateDTO = mediaDTO.toBuilder()
                .mediaId(mediaId)
                .postId(postId)
                .ownerId(userId)
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

        return postId;
    }
}
