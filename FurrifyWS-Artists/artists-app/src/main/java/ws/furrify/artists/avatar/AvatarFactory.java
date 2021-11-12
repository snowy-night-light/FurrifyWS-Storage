package ws.furrify.artists.avatar;

import ws.furrify.artists.avatar.dto.AvatarDTO;

import java.time.ZonedDateTime;
import java.util.UUID;

final class AvatarFactory {

    Avatar from(AvatarDTO avatarDTO) {
        AvatarSnapshot avatarSnapshot = AvatarSnapshot.builder()
                .id(avatarDTO.getId())
                .avatarId(
                        (avatarDTO.getAvatarId() != null) ? avatarDTO.getAvatarId() : UUID.randomUUID()
                )
                .artistId(avatarDTO.getArtistId())
                .ownerId(avatarDTO.getOwnerId())
                .filename(avatarDTO.getFilename())
                .md5(avatarDTO.getMd5())
                .extension(avatarDTO.getExtension())
                .fileUri(avatarDTO.getFileUri())
                .thumbnailUri(avatarDTO.getThumbnailUri())
                .createDate(
                        (avatarDTO.getCreateDate() != null) ? avatarDTO.getCreateDate() : ZonedDateTime.now()
                )
                .build();

        return Avatar.restore(avatarSnapshot);
    }

}
