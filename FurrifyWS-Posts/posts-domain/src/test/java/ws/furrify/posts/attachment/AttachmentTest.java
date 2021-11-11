package ws.furrify.posts.attachment;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.posts.attachment.vo.AttachmentSource;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AttachmentTest {

    private AttachmentSnapshot attachmentSnapshot;
    private Attachment attachment;

    @SneakyThrows
    @BeforeEach
    void setUp() {
        attachmentSnapshot = AttachmentSnapshot.builder()
                .id(0L)
                .attachmentId(UUID.randomUUID())
                .postId(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .filename("file.psd")
                .fileUrl(new URI("/test"))
                .md5("3c518eeb674c71b30297f072fde7eba5")
                .extension(AttachmentExtension.PSD)
                .sources(Collections.singleton(
                        new AttachmentSource(
                                UUID.randomUUID(),
                                "DeviantArtV1SourceStrategy",
                                new HashMap<>()
                        )
                ))
                .createDate(ZonedDateTime.now())
                .build();

        attachment = Attachment.restore(attachmentSnapshot);
    }

    @Test
    @DisplayName("Attachment restore from snapshot")
    void restore() {
        // Given attachmentSnapshot
        // When restore() method called
        Attachment attachment = Attachment.restore(attachmentSnapshot);
        // Then restore all values into aggregate and back to snapshot without losing data
        assertEquals(attachmentSnapshot, attachment.getSnapshot(), "Data was lost in restore call.");
    }

    @Test
    @DisplayName("Get snapshot from attachment")
    void getSnapshot() {
        // Given attachment
        // When getSnapshot() method called
        AttachmentSnapshot attachmentSnapshot = attachment.getSnapshot();
        // Then get snapshot of current data in aggregate
        assertEquals(this.attachmentSnapshot, attachmentSnapshot, "Data was lost in snapshot.");
    }

    @Test
    @DisplayName("Remove source")
    void removeSource() {
        // Given sourceId
        UUID sourceId = ((AttachmentSource) attachmentSnapshot.getSources().toArray()[0]).getSourceId();
        // When removeSource() method called
        // Then remove source from sources
        attachment.deleteSource(sourceId);

        assertEquals(
                0,
                attachment.getSnapshot().getSources().size(),
                "Source was not removed."
        );
    }

    @Test
    @DisplayName("Update source details in sources")
    void updateSourceDataInSources() {
        // Given new AttachmentSource
        AttachmentSource attachmentSource = new AttachmentSource(
                ((AttachmentSource) attachmentSnapshot.getSources().toArray()[0]).getSourceId(),
                "PatreonV1SourceStrategy",
                new HashMap<>() {{
                    put("asd", "ds");
                }}
        );
        // When updateSourceDataInSources() method called
        // Then update source data in attachment
        attachment.updateSourceDataInSources(attachmentSource);

        assertEquals(
                attachmentSource,
                attachment.getSnapshot().getSources().toArray()[0],
                "Source was not updated."
        );
    }

    @Test
    @DisplayName("Update source data in sources with non existing sourceId")
    void updateSourceDataInSources2() throws MalformedURLException {
        // Given new AttachmentSource with non-existing
        AttachmentSource attachmentSource = new AttachmentSource(
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
                () -> attachment.updateSourceDataInSources(attachmentSource),
                "Exception was not thrown."
        );
    }
}