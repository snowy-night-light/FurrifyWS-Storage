package ws.furrify.posts.attachment;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.posts.attachment.vo.AttachmentFile;
import ws.furrify.posts.attachment.vo.AttachmentSource;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
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
                .fileUri(new URI("/test"))
                .md5("3c518eeb674c71b30297f072fde7eba5")
                .extension(AttachmentExtension.EXTENSION_PSD)
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
        // Given new AttachmentSource with non-existing source id
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

    @Test
    @DisplayName("Replace attachment file")
    void replaceAttachmentFile() throws URISyntaxException {
        // Given new AttachmentFile
        AttachmentFile attachmentFile = AttachmentFile.builder()
                .fileUri(new URI("/"))
                .md5("08c6a51dde006e64aed953b94fd68f0c")
                .filename("dsa.psd")
                .extension(AttachmentExtension.EXTENSION_PSD)
                .build();
        // When replaceAttachmentFile() method called
        // Then attachment file changed in aggregate
        attachment.replaceAttachmentFile(attachmentFile);

        assertAll(
                () -> assertEquals(
                        attachmentFile.getFileUri(),
                        attachment.getSnapshot().getFileUri(),
                        "Values are different."
                ),
                () -> assertEquals(
                        attachmentFile.getMd5(),
                        attachment.getSnapshot().getMd5(),
                        "Values are different."
                ),
                () -> assertEquals(
                        attachmentFile.getFilename(),
                        attachment.getSnapshot().getFilename(),
                        "Values are different."
                ),
                () -> assertEquals(
                        attachmentFile.getExtension(),
                        attachment.getSnapshot().getExtension(),
                        "Values are different."
                )
        );
    }
}