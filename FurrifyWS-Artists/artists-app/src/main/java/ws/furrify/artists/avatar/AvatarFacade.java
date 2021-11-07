package ws.furrify.artists.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.artists.artist.ArtistEvent;
import ws.furrify.artists.avatar.dto.AvatarDTO;
import ws.furrify.artists.avatar.dto.AvatarDtoFactory;
import ws.furrify.posts.avatar.AvatarEvent;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.UUID;

/**
 * @author Skyte
 */
@RequiredArgsConstructor
@Log
final public class AvatarFacade {

    private final CreateAvatar createAvatarImpl;
    private final DeleteAvatar deleteAvatarImpl;
    private final AvatarRepository avatarRepository;
    private final AvatarFactory avatarFactory;
    private final AvatarDtoFactory avatarDTOFactory;

    /**
     * Handle incoming avatar events.
     *
     * @param avatarEvent Avatar event instance received from kafka.
     */
    public void handleEvent(final UUID key, final AvatarEvent avatarEvent) {
        AvatarDTO avatarDTO = avatarDTOFactory.from(key, avatarEvent);

        switch (DomainEventPublisher.AvatarEventType.valueOf(avatarEvent.getState())) {
            case CREATED -> saveAvatarInDatabase(avatarDTO);
            case REMOVED -> deleteAvatarByOwnerIdAndAvatarIdFromDatabase(key, avatarDTO.getAvatarId());

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
            case REMOVED -> deleteAvatarByOwnerIdAndArtistIdFromDatabase(
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

    private void deleteAvatarByOwnerIdAndAvatarIdFromDatabase(final UUID ownerId, final UUID avatarId) {
        avatarRepository.deleteByOwnerIdAndAvatarId(ownerId, avatarId);
    }

    private void deleteAvatarByOwnerIdAndArtistIdFromDatabase(final UUID ownerId, final UUID artistId) {
        avatarRepository.deleteByOwnerIdAndArtistId(ownerId, artistId);
    }
}
