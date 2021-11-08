package ws.furrify.artists.avatar.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ws.furrify.artists.avatar.AvatarExtension;
import ws.furrify.artists.avatar.AvatarQueryRepository;
import ws.furrify.posts.avatar.AvatarEvent;

import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Creates AvatarDTO from AvatarEvent.
 *
 * @author Skyte
 */
@RequiredArgsConstructor
public class AvatarDtoFactory {

    private final AvatarQueryRepository avatarQueryRepository;

    @SneakyThrows
    public AvatarDTO from(UUID key, AvatarEvent avatarEvent) {
        Instant createDateInstant = avatarEvent.getData().getCreateDate();
        ZonedDateTime createDate = null;

        if (createDateInstant != null) {
            createDate = createDateInstant.atZone(ZoneId.systemDefault());
        }

        var avatarId = UUID.fromString(avatarEvent.getAvatarId());

        return AvatarDTO.builder()
                .id(
                        avatarQueryRepository.getIdByAvatarId(avatarId)
                )
                .avatarId(avatarId)
                .artistId(UUID.fromString(avatarEvent.getData().getArtistId()))
                .ownerId(key)
                .extension(
                        (avatarEvent.getData().getExtension() != null) ?
                                AvatarExtension.valueOf(avatarEvent.getData().getExtension()) :
                                null
                )
                .filename(avatarEvent.getData().getFilename())
                .fileUrl(
                        (avatarEvent.getData().getFileUrl() != null) ?
                                new URL(avatarEvent.getData().getFileUrl()) :
                                null
                )
                .md5(avatarEvent.getData().getMd5())
                .thumbnailUrl(
                        (avatarEvent.getData().getThumbnailUrl() != null) ?
                                new URL(avatarEvent.getData().getThumbnailUrl()) :
                                null

                )
                .createDate(createDate)
                .build();
    }

}
