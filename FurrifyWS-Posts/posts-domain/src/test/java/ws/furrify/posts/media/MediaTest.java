package ws.furrify.posts.media;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.posts.media.vo.MediaPriority;
import ws.furrify.posts.media.vo.MediaSource;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MediaTest {

    private MediaSnapshot mediaSnapshot;
    private Media media;

    @SneakyThrows
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
                .fileUrl(new URI("/test"))
                .thumbnailUrl(new URI("/test"))
                .extension(MediaExtension.PNG)
                .sources(Collections.singleton(
                        new MediaSource(
                                UUID.randomUUID(),
                                "DeviantArtV1SourceStrategy",
                                new HashMap<>()
                        )
                ))
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

    @Test
    @DisplayName("Remove source")
    void removeSource() {
        // Given sourceId
        UUID sourceId = ((MediaSource) mediaSnapshot.getSources().toArray()[0]).getSourceId();
        // When removeSource() method called
        // Then remove source from sources
        media.deleteSource(sourceId);

        assertEquals(
                0,
                media.getSnapshot().getSources().size(),
                "Source was not removed."
        );
    }

    @Test
    @DisplayName("Update source details in sources")
    void updateSourceDataInSources() {
        // Given new MediaSource
        MediaSource mediaSource = new MediaSource(
                ((MediaSource) mediaSnapshot.getSources().toArray()[0]).getSourceId(),
                "PatreonV1SourceStrategy",
                new HashMap<>() {{
                    put("asd", "ds");
                }}
        );
        // When updateSourceDataInSources() method called
        // Then update source data in media
        media.updateSourceDataInSources(mediaSource);

        assertEquals(
                mediaSource,
                media.getSnapshot().getSources().toArray()[0],
                "Source was not updated."
        );
    }

    @Test
    @DisplayName("Update source data in sources with non existing sourceId")
    void updateSourceDataInSources2() throws MalformedURLException {
        // Given new MediaSource with non-existing
        MediaSource mediaSource = new MediaSource(
                UUID.randomUUID(),
                "PatreonV1SourceStrategy",
                new HashMap<>() {{
                    put("asd", "ds");
                }}
        );
        // When updateSourceDataInSources() method called
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                () -> media.updateSourceDataInSources(mediaSource),
                "Exception was not thrown."
        );
    }
}