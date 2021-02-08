package ws.furrify.artists.artist;

import lombok.RequiredArgsConstructor;
import ws.furrify.artists.ArtistEvent;
import ws.furrify.artists.vo.ArtistData;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
class DeleteArtistAdapter implements DeleteArtistPort {

    private final ArtistRepository artistRepository;
    private final DomainEventPublisher<ArtistEvent> domainEventPublisher;

    @Override
    public void deleteArtist(final UUID ownerId, final UUID artistId) {
        if (!artistRepository.existsByOwnerIdAndArtistId(ownerId, artistId)) {
            throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(artistId.toString()));
        }

        // Publish delete post event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.ARTIST,
                // Use ownerId as key
                ownerId,
                createArtistEvent(artistId)
        );
    }

    private ArtistEvent createArtistEvent(final UUID artistId) {
        return ArtistEvent.newBuilder()
                .setState(DomainEventPublisher.ArtistEventType.REMOVED.name())
                .setArtistId(artistId.toString())
                .setDataBuilder(ArtistData.newBuilder())
                .setOccurredOn(Instant.now().toEpochMilli())
                .build();
    }
}
