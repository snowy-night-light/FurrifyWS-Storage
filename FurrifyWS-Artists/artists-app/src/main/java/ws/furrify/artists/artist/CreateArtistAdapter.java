package ws.furrify.artists.artist;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ws.furrify.artists.artist.dto.ArtistDTO;
import ws.furrify.artists.artist.vo.ArtistNickname;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class CreateArtistAdapter implements CreateArtistPort {

    private final ArtistRepository artistRepository;
    private final ArtistFactory artistFactory;
    private final DomainEventPublisher<ArtistEvent> eventPublisher;

    @Override
    public UUID createArtist(@NonNull final UUID ownerId,
                             @NonNull final ArtistDTO artistDTO) {
        // Generate artist UUID
        UUID artistId = UUID.randomUUID();

        // Update artistDTO and create Artist from that data
        Artist artist = artistFactory.from(
                artistDTO.toBuilder()
                        .artistId(artistId)
                        .ownerId(ownerId)
                        .createDate(ZonedDateTime.now())
                        .build()
        );
        // Update nicknames array
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
                        DomainEventPublisher.ArtistEventType.CREATED,
                        artist
                )
        );


        return artistId;
    }
}
