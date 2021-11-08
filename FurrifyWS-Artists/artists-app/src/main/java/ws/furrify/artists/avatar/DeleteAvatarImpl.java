package ws.furrify.artists.avatar;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ws.furrify.posts.avatar.AvatarEvent;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.UUID;

@RequiredArgsConstructor
final class DeleteAvatarImpl implements DeleteAvatar {

    private final AvatarRepository avatarRepository;
    private final DomainEventPublisher<AvatarEvent> domainEventPublisher;

    @Override
    public void deleteAvatar(@NonNull final UUID userId,
                             @NonNull final UUID artistId,
                             @NonNull final UUID avatarId) {
        if (!avatarRepository.existsByOwnerIdAndArtistIdAndAvatarId(userId, artistId, avatarId)) {
            throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(avatarId.toString()));
        }

        // Publish delete avatar event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.AVATAR,
                // Use userId as key
                userId,
                AvatarUtils.deleteAvatarEvent(artistId, avatarId)
        );
    }
}
