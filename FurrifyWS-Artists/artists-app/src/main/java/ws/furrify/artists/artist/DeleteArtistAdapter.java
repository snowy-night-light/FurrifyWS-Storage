package ws.furrify.artists.artist;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.UUID;

@RequiredArgsConstructor
class DeleteArtistAdapter implements DeleteArtistPort {

    private final ArtistRepository artistRepository;
    private final DomainEventPublisher<ArtistEvent> domainEventPublisher;

    @Override
    public void deleteArtist(@NonNull final UUID ownerId,
                             @NonNull final UUID artistId) {
        if (!artistRepository.existsByOwnerIdAndArtistId(ownerId, artistId)) {
            throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(artistId.toString()));
        }

        // Publish delete post event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.ARTIST,
                // Use ownerId as key
                ownerId,
                ArtistUtils.deleteArtistEvent(artistId)
        );
    }
}
