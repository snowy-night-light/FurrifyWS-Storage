package ws.furrify.posts.media;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.posts.media.vo.MediaPriority;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MediaTest {

    private MediaSnapshot mediaSnapshot;
    private Media media;

    @BeforeEach
    void setUp() {
        mediaSnapshot = MediaSnapshot.builder()
                .id(0L)
                .mediaId(UUID.randomUUID())
                .postId(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .priority(3)
                .filename("file.png")
                .md5("3c518eeb674c71b30297f072fde7eba5")
                .extension(MediaExtension.PNG)
                .status(MediaStatus.REQUEST_PENDING)
                .createDate(ZonedDateTime.now())
                .build();

        media = Media.restore(mediaSnapshot);
    }

    @Test
    @DisplayName("Media restore from snapshot")
    void restore() {
        // Given mediaSnapshot
        // When restore() method called
        Media media = Media.restore(mediaSnapshot);
        // Then restore all values into aggregate and back to snapshot without losing data
        assertEquals(mediaSnapshot, media.getSnapshot(), "Data was lost in restore call.");
    }

    @Test
    @DisplayName("Get snapshot from media")
    void getSnapshot() {
        // Given media
        // When getSnapshot() method called
        MediaSnapshot mediaSnapshot = media.getSnapshot();
        // Then get snapshot of current data in aggregate
        assertEquals(this.mediaSnapshot, mediaSnapshot, "Data was lost in snapshot.");
    }

    @Test
    @DisplayName("Replace priority")
    void replacePriority() {
        // Given new priority
        Integer newPriority = 3;
        // When replacePriority() method called
        // Then replace priority
        media.replacePriority(
                MediaPriority.of(newPriority)
        );

        assertEquals(newPriority, media.getSnapshot().getPriority(), "Priority was not updated");
    }
}