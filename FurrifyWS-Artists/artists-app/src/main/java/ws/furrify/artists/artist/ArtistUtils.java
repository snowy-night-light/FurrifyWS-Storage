package ws.furrify.artists.artist;

import ws.furrify.artists.artist.vo.ArtistData;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Utils class regarding Artist entity.
 *
 * @author Skyte
 */
class ArtistUtils {

    /**
     * Create ArtistEvent by given aggregate and set its state change.
     *
     * @param eventType Event state change type.
     * @param artist    Artist aggregate to build artist event from.
     * @return Created artist event.
     */
    public static ArtistEvent createArtistEvent(final DomainEventPublisher.ArtistEventType eventType,
                                                final Artist artist) {
        ArtistSnapshot artistSnapshot = artist.getSnapshot();

        return ArtistEvent.newBuilder()
                .setState(eventType.name())
                .setArtistId(artistSnapshot.getArtistId().toString())
                .setData(
                        ArtistData.newBuilder()
                                .setOwnerId(artistSnapshot.getOwnerId().toString())
                                .setNicknames(new ArrayList<>(artistSnapshot.getNicknames()))
                                .setPreferredNickname(artistSnapshot.getPreferredNickname())
                                .setCreateDate(artistSnapshot.getCreateDate().toInstant().toEpochMilli())
                                .build()
                )
                .setOccurredOn(Instant.now().toEpochMilli())
                .build();
    }

    /**
     * Create ArtistEvent with REMOVE state.
     *
     * @param artistId ArtistId the delete event will regard.
     * @return Created artist event.
     */
    public static ArtistEvent deleteArtistEvent(final UUID artistId) {
        return ArtistEvent.newBuilder()
                .setState(DomainEventPublisher.ArtistEventType.REMOVED.name())
                .setArtistId(artistId.toString())
                .setDataBuilder(ArtistData.newBuilder())
                .setOccurredOn(Instant.now().toEpochMilli())
                .build();
    }

}
