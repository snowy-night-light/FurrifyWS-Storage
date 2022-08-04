package ws.furrify.artists.avatar;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.artists.avatar.vo.AvatarFile;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AvatarTest {

    private AvatarSnapshot avatarSnapshot;
    private Avatar avatar;

    @SneakyThrows
    @BeforeEach
    void setUp() {
        avatarSnapshot = AvatarSnapshot.builder()
                .id(0L)
                .avatarId(UUID.randomUUID())
                .artistId(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .filename("file.png")
                .md5("3c518eeb674c71b30297f072fde7eba5")
                .fileUri(new URI("/test"))
                .thumbnailUri(new URI("/test"))
                .extension(AvatarExtension.EXTENSION_PNG)
                .createDate(ZonedDateTime.now())
                .build();

        avatar = Avatar.restore(avatarSnapshot);
    }

    @Test
    @DisplayName("Avatar restore from snapshot")
    void restore() {
        // Given avatarSnapshot
        // When restore() method called
        Avatar avatar = Avatar.restore(avatarSnapshot);
        // Then restore all values into aggregate and back to snapshot without losing data
        assertEquals(avatarSnapshot, avatar.getSnapshot(), "Data was lost in restore call.");
    }

    @Test
    @DisplayName("Get snapshot from avatar")
    void getSnapshot() {
        // Given avatar
        // When getSnapshot() method called
        AvatarSnapshot avatarSnapshot = avatar.getSnapshot();
        // Then get snapshot of current data in aggregate
        assertEquals(this.avatarSnapshot, avatarSnapshot, "Data was lost in snapshot.");
    }

    @Test
    @DisplayName("Replace avatar file")
    void replaceAvatarFile() throws URISyntaxException {
        // Given new AvatarFile
        AvatarFile avatarFile = AvatarFile.builder()
                .fileUri(new URI("/"))
                .thumbnailUri(new URI("/"))
                .md5("08c6a51dde006e64aed953b94fd68f0c")
                .filename("dsa.png")
                .extension(AvatarExtension.EXTENSION_PNG)
                .build();
        // When replaceAvatarFile() method called
        // Then avatar file changed in aggregate
        avatar.replaceAvatarFile(avatarFile);

        assertAll(
                () -> assertEquals(
                        avatarFile.getFileUri(),
                        avatar.getSnapshot().getFileUri(),
                        "Values are different."
                ),
                () -> assertEquals(
                        avatarFile.getThumbnailUri(),
                        avatar.getSnapshot().getThumbnailUri(),
                        "Values are different."
                ),
                () -> assertEquals(
                        avatarFile.getMd5(),
                        avatar.getSnapshot().getMd5(),
                        "Values are different."
                ),
                () -> assertEquals(
                        avatarFile.getFilename(),
                        avatar.getSnapshot().getFilename(),
                        "Values are different."
                ),
                () -> assertEquals(
                        avatarFile.getExtension(),
                        avatar.getSnapshot().getExtension(),
                        "Values are different."
                )
        );
    }
}