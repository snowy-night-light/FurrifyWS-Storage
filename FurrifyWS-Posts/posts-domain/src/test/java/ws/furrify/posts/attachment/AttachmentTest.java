package ws.furrify.posts.attachment;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                .fileUrl(new URL("https://example.com//"))
                .md5("3c518eeb674c71b30297f072fde7eba5")
                .extension(AttachmentExtension.PSD)
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
}