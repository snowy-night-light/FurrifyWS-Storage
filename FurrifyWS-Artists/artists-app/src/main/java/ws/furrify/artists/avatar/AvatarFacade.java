package ws.furrify.artists.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.artists.artist.ArtistEvent;
import ws.furrify.artists.avatar.dto.AvatarDTO;
import ws.furrify.artists.avatar.dto.AvatarDtoFactory;
import ws.furrify.artists.avatar.strategy.AvatarUploadStrategy;
import ws.furrify.posts.avatar.AvatarEvent;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.UUID;

/**
 * @author Skyte
 */
@RequiredArgsConstructor
@Log
final public class AvatarFacade {

    private final CreateAvatar createAvatarImpl;
    private final ReplaceAvatar replaceAvatarImpl;
    private final UpdateAvatar updateAvatarImpl;
    private final DeleteAvatar deleteAvatarImpl;
    private final AvatarRepository avatarRepository;
    private final AvatarFactory avatarFactory;
    private final AvatarDtoFactory avatarDTOFactory;
    private final AvatarUploadStrategy avatarUploadStrategy;

    /**
     * Handle incoming avatar events.
     *
     * @param avatarEvent Avatar event instance received from kafka.
     */
    public void handleEvent(final UUID key, final AvatarEvent avatarEvent) {
        AvatarDTO avatarDTO = avatarDTOFactory.from(key, avatarEvent);

        switch (DomainEventPublisher.AvatarEventType.valueOf(avatarEvent.getState())) {
            case CREATED, UPDATED, REPLACED -> saveAvatarInDatabase(avatarDTO);
            case REMOVED -> deleteAvatarByAvatarIdFromDatabaseAndFiles(
                    key,
                    avatarDTO.getAvatarId()
            );

            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + avatarEvent.getState() + " Topic=avatar_events");
        }
    }

    /**
     * Handle incoming artist events.
     *
     * @param artistEvent Artist event instance received from kafka.
     */
    public void handleEvent(final UUID key, final ArtistEvent artistEvent) {
        switch (DomainEventPublisher.ArtistEventType.valueOf(artistEvent.getState())) {
            case REMOVED -> deleteAvatarByOwnerIdAndArtistIdFromDatabaseAndRemoveFiles(
                    key,
                    UUID.fromString(artistEvent.getArtistId())
            );

            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + artistEvent.getState() + " Topic=artist_events");
        }
    }

    /**
     * Creates avatar.
     *
     * @param userId    User uuid to assign media to.
     * @param artistId  Artist uuid to assign media to.
     * @param avatarDTO Avatar to create.
     * @return Created avatar UUID.
     */
    public UUID createAvatar(final UUID userId,
                             final UUID artistId,
                             final AvatarDTO avatarDTO,
                             final MultipartFile mediaFile) {
        return createAvatarImpl.createAvatar(userId, artistId, avatarDTO, mediaFile);
    }

    /**
     * Replaces avatar.
     *
     * @param userId    User uuid to assign avatar to.
     * @param artistId  Artist uuid to assign avatar to.
     * @param avatarDTO Avatar to replace.
     */
    public void replaceAvatar(final UUID userId,
                              final UUID artistId,
                              final UUID avatarId,
                              final AvatarDTO avatarDTO,
                              final MultipartFile avatarFile) {
        replaceAvatarImpl.replaceAvatar(userId, artistId, avatarId, avatarDTO, avatarFile);
    }

    /**
     * Updates avatar.
     *
     * @param userId    User uuid to assign avatar to.
     * @param artistId  Artist uuid to assign avatar to.
     * @param avatarDTO Avatar to update.
     */
    public void updateAvatar(final UUID userId,
                             final UUID artistId,
                             final UUID avatarId,
                             final AvatarDTO avatarDTO,
                             final MultipartFile avatarFile) {
        updateAvatarImpl.updateAvatar(userId, artistId, avatarId, avatarDTO, avatarFile);
    }

    /**
     * Deletes avatar.
     *
     * @param userId   Avatar owner UUID.
     * @param artistId Artist UUID.
     * @param avatarId Avatar UUID.
     */
    public void deleteAvatar(final UUID userId, final UUID artistId, final UUID avatarId) {
        deleteAvatarImpl.deleteAvatar(userId, artistId, avatarId);
    }

    private void saveAvatarInDatabase(final AvatarDTO avatarDTO) {
        avatarRepository.save(avatarFactory.from(avatarDTO));
    }

    private void deleteAvatarByAvatarIdFromDatabaseAndFiles(final UUID ownerId, final UUID avatarId) {
        avatarUploadStrategy.removeAllAvatarFiles(avatarId);

        avatarRepository.deleteByOwnerIdAndAvatarId(ownerId, avatarId);
    }

    private void deleteAvatarByOwnerIdAndArtistIdFromDatabaseAndRemoveFiles(final UUID ownerId, final UUID artistId) {
        Avatar avatar = avatarRepository.findByOwnerIdAndArtistId(ownerId, artistId)
                .orElseThrow(() -> new IllegalStateException(Errors.NO_RECORD_FOUND.getErrorMessage(artistId)));

        // Remove files from storage
        avatarUploadStrategy.removeAllAvatarFiles(avatar.getSnapshot().getAvatarId());

        avatarRepository.deleteByOwnerIdAndArtistId(ownerId, artistId);
    }
}
