package ws.furrify.artists.avatar;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.artists.artist.ArtistServiceClient;
import ws.furrify.artists.avatar.dto.AvatarDTO;
import ws.furrify.artists.avatar.strategy.AvatarUploadStrategy;
import ws.furrify.posts.avatar.AvatarEvent;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.FileContentIsCorruptedException;
import ws.furrify.shared.exception.FileExtensionIsNotMatchingContentException;
import ws.furrify.shared.exception.RecordAlreadyExistsException;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.io.IOException;
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

        // Check if file is matching declared extension
        boolean isFileValid = AvatarExtension.isValidFile(
                avatarFile.getOriginalFilename(),
                avatarFile,
                avatarDTO.getExtension()
        );

        if (!isFileValid) {
            throw new FileExtensionIsNotMatchingContentException(Errors.FILE_EXTENSION_IS_NOT_MATCHING_CONTENT.getErrorMessage());
        }

        String md5;
        try {
            // Get file hash
            md5 = DigestUtils.md5Hex(avatarFile.getInputStream());
        } catch (IOException e) {
            throw new FileContentIsCorruptedException(Errors.FILE_CONTENT_IS_CORRUPTED.getErrorMessage());
        }

        // Upload file and generate thumbnail
        AvatarUploadStrategy.UploadedAvatarFile uploadedAvatarFile =
                avatarUploadStrategy.uploadAvatarWithGeneratedThumbnail(artistId, avatarId, avatarFile);

        // Edit avatarDTO with generated avatar uuid
        AvatarDTO updatedAvatarToCreateDTO = avatarDTO.toBuilder()
                .avatarId(avatarId)
                .artistId(artistId)
                .ownerId(userId)
                .filename(avatarFile.getOriginalFilename())
                .fileUrl(uploadedAvatarFile.getFileUrl())
                .thumbnailUrl(uploadedAvatarFile.getThumbnailUrl())
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