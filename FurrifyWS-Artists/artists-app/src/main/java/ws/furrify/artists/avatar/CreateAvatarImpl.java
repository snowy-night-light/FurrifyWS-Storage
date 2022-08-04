package ws.furrify.artists.avatar;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.artists.artist.ArtistServiceClient;
import ws.furrify.artists.avatar.dto.AvatarDTO;
import ws.furrify.artists.avatar.strategy.AvatarUploadStrategy;
import ws.furrify.posts.avatar.AvatarEvent;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordAlreadyExistsException;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.time.ZonedDateTime;
import java.util.UUID;

@RequiredArgsConstructor
final class CreateAvatarImpl implements CreateAvatar {

    private final ArtistServiceClient artistService;
    private final AvatarRepository avatarRepository;
    private final AvatarFactory avatarFactory;
    private final AvatarUploadStrategy avatarUploadStrategy;
    private final DomainEventPublisher<AvatarEvent> domainEventPublisher;

    @Override
    public UUID createAvatar(@NonNull final UUID userId,
                             @NonNull final UUID artistId,
                             @NonNull final AvatarDTO avatarDTO,
                             @NonNull final MultipartFile avatarFile) {
        if (artistService.getUserArtist(userId, artistId) == null) {
            throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(artistId.toString()));
        }

        // If avatar already exists
        if (avatarRepository.existsByOwnerIdAndArtistId(userId, artistId)) {
            throw new RecordAlreadyExistsException(Errors.RECORD_ALREADY_EXISTS.getErrorMessage(artistId.toString()));
        }

        // Generate avatar uuid
        UUID avatarId = UUID.randomUUID();

        // Generate hash from file
        final String md5 = AvatarFileUtils.generateMd5FromFile(avatarFile);

        // Validate avatar file
        AvatarFileUtils.validateAvatar(
                avatarDTO.getExtension(),
                avatarFile
        );

        // Upload file and generate thumbnail
        AvatarUploadStrategy.UploadedAvatarFile uploadedAvatarFile =
                avatarUploadStrategy.uploadAvatarWithGeneratedThumbnail(avatarId, avatarFile);

        // Edit avatarDTO with generated avatar uuid
        AvatarDTO updatedAvatarToCreateDTO = avatarDTO.toBuilder()
                .avatarId(avatarId)
                .artistId(artistId)
                .ownerId(userId)
                .filename(avatarFile.getOriginalFilename())
                .fileUri(uploadedAvatarFile.getFileUri())
                .thumbnailUri(uploadedAvatarFile.getThumbnailUri())
                .md5(md5)
                .createDate(ZonedDateTime.now())
                .build();

        // Publish create avatar event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.AVATAR,
                // User userId as key
                userId,
                AvatarUtils.createAvatarEvent(
                        DomainEventPublisher.AvatarEventType.CREATED,
                        avatarFactory.from(updatedAvatarToCreateDTO)
                )
        );

        return avatarId;
    }
}