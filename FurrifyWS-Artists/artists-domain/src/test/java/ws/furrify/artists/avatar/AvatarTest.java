package ws.furrify.artists.avatar;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.UUID;

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
}