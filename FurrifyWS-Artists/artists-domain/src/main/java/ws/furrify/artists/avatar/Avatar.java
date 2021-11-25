package ws.furrify.artists.avatar;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.java.Log;
import ws.furrify.artists.avatar.vo.AvatarFile;

import java.time.ZonedDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
@Log
class Avatar {
    private final Long id;
    @NonNull
    private final UUID mediaId;
    @NonNull
    private final UUID postId;
    @NonNull
    private final UUID ownerId;
    @NonNull
    private final AvatarFile file;

    private final ZonedDateTime createDate;

    static Avatar restore(AvatarSnapshot avatarSnapshot) {
        return new Avatar(
                avatarSnapshot.getId(),
                avatarSnapshot.getAvatarId(),
                avatarSnapshot.getArtistId(),
                avatarSnapshot.getOwnerId(),
                AvatarFile.builder()
                        .extension(avatarSnapshot.getExtension())
                        .thumbnailUri(avatarSnapshot.getThumbnailUri())
                        .filename(avatarSnapshot.getFilename())
                        .md5(avatarSnapshot.getMd5())
                        .fileUri(avatarSnapshot.getFileUri())
                        .build(),
                avatarSnapshot.getCreateDate()
        );
    }

    AvatarSnapshot getSnapshot() {
        return AvatarSnapshot.builder()
                .id(id)
                .avatarId(mediaId)
                .artistId(postId)
                .ownerId(ownerId)
                .extension(file.getExtension())
                .thumbnailUri(file.getThumbnailUri())
                .filename(file.getFilename())
                .md5(file.getMd5())
                .fileUri(file.getFileUri())
                .createDate(createDate)
                .build();
    }
}