package ws.furrify.artists.artist;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import ws.furrify.artists.artist.dto.ArtistDTO;
import ws.furrify.artists.artist.dto.ArtistDtoFactory;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.UUID;

/**
 * @author Skyte
 */
@RequiredArgsConstructor
@Log
public class ArtistFacade {

    private final CreateArtistPort createArtistAdapter;
    private final DeleteArtistPort deleteArtistAdapter;
    private final UpdateArtistPort updateArtistAdapter;
    private final ReplaceArtistPort replaceArtistAdapter;
    private final ArtistRepository artistRepository;
    private final ArtistFactory artistFactory;
    private final ArtistDtoFactory artistDTOFactory;


    /**
     * Handle incoming artist events.
     *
     * @param artistEvent Artist event instance received from kafka.
     */
    void handleEvent(final UUID key, final ArtistEvent artistEvent) {
        ArtistDTO artistDTO = artistDTOFactory.from(key, artistEvent);

        switch (DomainEventPublisher.ArtistEventType.valueOf(artistEvent.getState())) {
            case CREATED, REPLACED, UPDATED -> saveArtist(artistDTO);
            case REMOVED -> deleteArtistByArtistId(artistDTO.getArtistId());

            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + artistEvent.getState() + " Topic=artist_events");
        }
    }

    /**
     * Creates Artist.
     *
     * @param ownerId   Owner of artist to be created.
     * @param artistDTO ArtistDTO.
     * @return Created record UUID.
     */
    public UUID createArtist(UUID ownerId, ArtistDTO artistDTO) {
        return createArtistAdapter.createArtist(ownerId, artistDTO);
    }

    /**
     * Replaces all fields in Artist.
     *
     * @param ownerId   Owner UUID of Artist.
     * @param artistId  Artist UUID to be replaced.
     * @param artistDTO ArtistDTO with replace details.
     */
    public void replaceArtist(UUID ownerId, UUID artistId, ArtistDTO artistDTO) {
        replaceArtistAdapter.replaceArtist(ownerId, artistId, artistDTO);
    }

    /**
     * Updates changed fields in Artist from DTO.
     *
     * @param ownerId   Owner UUID of Artist.
     * @param artistId  Artist UUID to be updated.
     * @param artistDTO ArtistDTO with some changes.
     */
    public void updateArtist(UUID ownerId, UUID artistId, ArtistDTO artistDTO) {
        updateArtistAdapter.updateArtist(ownerId, artistId, artistDTO);
    }

    /**
     * Deletes Artist.
     *
     * @param ownerId  Owner UUID of Artist.
     * @param artistId Artist UUID.
     */
    public void deleteArtist(final UUID ownerId, final UUID artistId) {
        deleteArtistAdapter.deleteArtist(ownerId, artistId);
    }

    private void deleteArtistByArtistId(final UUID artistId) {
        artistRepository.deleteByArtistId(artistId);
    }

    private void saveArtist(final ArtistDTO artistDTO) {
        artistRepository.save(artistFactory.from(artistDTO));
    }

}
