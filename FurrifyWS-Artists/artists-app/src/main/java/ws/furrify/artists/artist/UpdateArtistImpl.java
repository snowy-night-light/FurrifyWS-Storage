package ws.furrify.artists.artist;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ws.furrify.artists.artist.dto.ArtistDTO;
import ws.furrify.artists.artist.vo.ArtistNickname;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
final class UpdateArtistImpl implements UpdateArtist {

    private final ArtistRepository artistRepository;
    private final DomainEventPublisher<ArtistEvent> eventPublisher;

    @Override
    public void updateArtist(@NonNull final UUID ownerId,
                             @NonNull final UUID artistId,
                             @NonNull final ArtistDTO artistDTO) {
        Artist artist = artistRepository.findByOwnerIdAndArtistId(ownerId, artistId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(artistId.toString())));

        // Update changed fields in artist
        if (artistDTO.getNicknames() != null || artistDTO.getPreferredNickname() != null) {
            // Convert nicknames to vo objects or get null.
            Set<ArtistNickname> newNicknames = Optional.ofNullable(artistDTO.getNicknames())
                    .map(nicknames -> nicknames.stream()
                            .map(ArtistNickname::of)
                            .collect(Collectors.toSet())
                    ).orElse(null);

            artist.updateNicknames(
                    newNicknames,
                    ArtistNickname.ofNullable(artistDTO.getPreferredNickname()),
                    artistRepository
            );
        }


        // Publish create artist event
        eventPublisher.publish(
                DomainEventPublisher.Topic.ARTIST,
                // Use ownerId as key
                ownerId,
                ArtistUtils.createArtistEvent(
                        DomainEventPublisher.ArtistEventType.UPDATED,
                        artist
                )
        );
    }
}
