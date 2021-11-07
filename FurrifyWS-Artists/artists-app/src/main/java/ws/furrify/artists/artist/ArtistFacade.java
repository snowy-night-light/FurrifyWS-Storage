package ws.furrify.artists.artist;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import ws.furrify.artists.artist.dto.ArtistDTO;
import ws.furrify.artists.artist.dto.ArtistDtoFactory;
import ws.furrify.artists.artist.vo.ArtistAvatar;
import ws.furrify.artists.artist.vo.ArtistSource;
import ws.furrify.posts.avatar.AvatarEvent;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.shared.vo.SourceOriginType;
import ws.furrify.sources.source.SourceEvent;

import java.net.URL;
import java.util.UUID;

/**
 * @author Skyte
 */
@RequiredArgsConstructor
@Log
final public class ArtistFacade {

    private final CreateArtist createArtistImpl;
    private final DeleteArtist deleteArtistImpl;
    private final UpdateArtist updateArtistImpl;
    private final ReplaceArtistImpl replaceArtistImpl;
    private final ArtistRepository artistRepository;
    private final ArtistFactory artistFactory;
    private final ArtistDtoFactory artistDTOFactory;


    /**
     * Handle incoming artist events.
     *
     * @param artistEvent Artist event instance received from kafka.
     */
    public void handleEvent(final UUID key, final ArtistEvent artistEvent) {
        ArtistDTO artistDTO = artistDTOFactory.from(key, artistEvent);

        switch (DomainEventPublisher.ArtistEventType.valueOf(artistEvent.getState())) {
            case CREATED, REPLACED, UPDATED -> saveArtistToDatabase(artistDTO);
            case REMOVED -> deleteArtistByArtistIdFromDatabase(artistDTO.getArtistId());

            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + artistEvent.getState() + " Topic=artist_events");
        }
    }

    /**
     * Handle incoming avatar events.
     *
     * @param avatarEvent Avatar event instance received from kafka.
     */
    @SneakyThrows
    public void handleEvent(final UUID key, final AvatarEvent avatarEvent) {
        switch (DomainEventPublisher.AvatarEventType.valueOf(avatarEvent.getState())) {
            case CREATED -> addAvatarToArtist(
                    key,
                    UUID.fromString(avatarEvent.getData().getArtistId()),
                    ArtistAvatar.builder()
                            .avatarId(UUID.fromString(avatarEvent.getAvatarId()))
                            .fileUrl(
                                    new URL(avatarEvent.getData().getFileUrl())
                            )
                            .extension(avatarEvent.getData().getExtension())
                            .thumbnailUrl(
                                    new URL(avatarEvent.getData().getThumbnailUrl())
                            )
                            .build()
            );
            case REMOVED -> deleteAvatarFromArtist(
                    key,
                    UUID.fromString(avatarEvent.getData().getArtistId()),
                    UUID.fromString(avatarEvent.getAvatarId())
            );

            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + avatarEvent.getState() + " Topic=artist_events");
        }
    }

    /**
     * Handle incoming source events.
     *
     * @param sourceEvent Source event instance received from kafka.
     */
    @SneakyThrows
    public void handleEvent(final UUID key, final SourceEvent sourceEvent) {
        UUID sourceId = UUID.fromString(sourceEvent.getSourceId());

        // Check if this source event origins from Artist
        if (!SourceOriginType.ARTIST.name().equals(sourceEvent.getData().getOriginType())) {
            return;
        }

        switch (DomainEventPublisher.SourceEventType.valueOf(sourceEvent.getState())) {
            case REMOVED -> deleteSourceFromArtist(
                    key,
                    UUID.fromString(sourceEvent.getData().getOriginId()),
                    sourceId
            );
            case UPDATED, REPLACED -> updateSourceDataInArtist(
                    key,
                    UUID.fromString(sourceEvent.getData().getOriginId()),
                    // Build post source from source event
                    ArtistSource.builder()
                            .sourceId(sourceId)
                            .strategy(sourceEvent.getData().getStrategy())
                            .data(sourceEvent.getData().getDataHashMap())
                            .build()
            );
            case CREATED -> addSourceToArtist(
                    key,
                    UUID.fromString(sourceEvent.getData().getOriginId()),
                    // Build post source from source event
                    ArtistSource.builder()
                            .sourceId(sourceId)
                            .strategy(sourceEvent.getData().getStrategy())
                            .data(sourceEvent.getData().getDataHashMap())
                            .build()
            );
            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + sourceEvent.getState() + " Topic=source_events");
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
        return createArtistImpl.createArtist(ownerId, artistDTO);
    }

    /**
     * Replaces all fields in Artist.
     *
     * @param ownerId   Owner UUID of Artist.
     * @param artistId  Artist UUID to be replaced.
     * @param artistDTO ArtistDTO with replace details.
     */
    public void replaceArtist(UUID ownerId, UUID artistId, ArtistDTO artistDTO) {
        replaceArtistImpl.replaceArtist(ownerId, artistId, artistDTO);
    }

    /**
     * Updates changed fields in Artist from DTO.
     *
     * @param ownerId   Owner UUID of Artist.
     * @param artistId  Artist UUID to be updated.
     * @param artistDTO ArtistDTO with some changes.
     */
    public void updateArtist(UUID ownerId, UUID artistId, ArtistDTO artistDTO) {
        updateArtistImpl.updateArtist(ownerId, artistId, artistDTO);
    }

    /**
     * Deletes Artist.
     *
     * @param ownerId  Owner UUID of Artist.
     * @param artistId Artist UUID.
     */
    public void deleteArtist(final UUID ownerId, final UUID artistId) {
        deleteArtistImpl.deleteArtist(ownerId, artistId);
    }

    private void deleteArtistByArtistIdFromDatabase(final UUID artistId) {
        artistRepository.deleteByArtistId(artistId);
    }

    private void saveArtistToDatabase(final ArtistDTO artistDTO) {
        artistRepository.save(artistFactory.from(artistDTO));
    }

    private void deleteSourceFromArtist(final UUID ownerId,
                                        final UUID artistId,
                                        final UUID sourceId) {
        Artist artist = artistRepository.findByOwnerIdAndArtistId(ownerId, artistId)
                .orElseThrow(() -> new IllegalStateException(Errors.NO_RECORD_FOUND.getErrorMessage(artistId)));
        artist.deleteSource(sourceId);

        artistRepository.save(artist);
    }

    private void updateSourceDataInArtist(final UUID ownerId,
                                          final UUID artistId,
                                          final ArtistSource artistSource) {
        Artist artist = artistRepository.findByOwnerIdAndArtistId(ownerId, artistId)
                .orElseThrow(() -> new IllegalStateException(Errors.NO_RECORD_FOUND.getErrorMessage(artistId)));
        artist.updateSourceDataInSources(artistSource);

        artistRepository.save(artist);
    }

    private void addSourceToArtist(final UUID ownerId,
                                   final UUID artistId,
                                   final ArtistSource artistSource) {
        Artist artist = artistRepository.findByOwnerIdAndArtistId(ownerId, artistId)
                .orElseThrow(() -> new IllegalStateException(Errors.NO_RECORD_FOUND.getErrorMessage(artistId)));
        artist.addSource(artistSource);

        artistRepository.save(artist);
    }

    private void addAvatarToArtist(final UUID ownerId,
                                   final UUID artistId,
                                   final ArtistAvatar artistAvatar) {
        Artist artist = artistRepository.findByOwnerIdAndArtistId(ownerId, artistId)
                .orElseThrow(() -> new IllegalStateException("Received request from kafka contains invalid uuid's."));
        artist.addAvatar(artistAvatar);

        artistRepository.save(artist);
    }

    private void deleteAvatarFromArtist(final UUID ownerId,
                                        final UUID artistId,
                                        final UUID avatarId) {
        Artist artist = artistRepository.findByOwnerIdAndArtistId(ownerId, artistId)
                .orElseThrow(() -> new IllegalStateException("Received request from kafka contains invalid uuid's."));
        artist.removeAvatar(avatarId);

        artistRepository.save(artist);
    }

}
