package ws.furrify.artists.artist;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ws.furrify.artists.artist.dto.ArtistDTO;
import ws.furrify.artists.artist.vo.ArtistNickname;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
final class ReplaceArtistImpl implements ReplaceArtist {

    private final ArtistRepository artistRepository;
    private final DomainEventPublisher<ArtistEvent> eventPublisher;

    @Override
    public void replaceArtist(@NonNull final UUID ownerId,
                              @NonNull final UUID artistId,
                              @NonNull final ArtistDTO artistDTO) {
        Artist artist = artistRepository.findByOwnerIdAndArtistId(ownerId, artistId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(artistId.toString())));

        // Replace fields in artist
        artist.updateNicknames(
                artistDTO.getNicknames().stream()
                        .map(ArtistNickname::of)
                        .collect(Collectors.toSet()),
                ArtistNickname.of(artistDTO.getPreferredNickname()),
                artistRepository
        );

        // Publish create artist event
        eventPublisher.publish(
                DomainEventPublisher.Topic.ARTIST,
                // Use ownerId as key
                ownerId,
                ArtistUtils.createArtistEvent(
                        DomainEventPublisher.ArtistEventType.REPLACED,
                        artist
                )
        );

    }
}
