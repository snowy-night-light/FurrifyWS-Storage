package ws.furrify.posts.media.vo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.posts.media.MediaExtension;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MediaFileTest {

    private static String filename;
    private static MediaExtension extension;
    private static URL fileUrl;
    private static URL thumbnailUrl;
    private static String fileHash;

    @BeforeAll
    static void setUp() throws MalformedURLException {
        filename = "test.png";
        extension = MediaExtension.PNG;
        fileUrl = new URL("https://google.pl");
        thumbnailUrl = new URL("https://google2.pl");
        fileHash = "3c518eeb674c71b30297f072fde7eba5";
    }

    @Test
    @DisplayName("Create MediaFile")
    void of() {
        // Given filename, extension, fileUrl, thumbnailUrl and fileHash
        // When builder build
        var mediaFile = MediaFile.builder()
                .filename(filename)
                .extension(extension)
                .fileUrl(fileUrl)
                .thumbnailUrl(thumbnailUrl)
                .fileHash(fileHash)
                .build();
        // Then return created MediaFile
        assertAll(
                () -> assertEquals(
                        filename,
                        mediaFile.getFilename(),
                        "Created filename is not the same."
                ),
                () -> assertEquals(
                        extension,
                        mediaFile.getExtension(),
                        "Created extension is not the same."
                ),
                () -> assertEquals(
                        fileUrl,
                        mediaFile.getFileUrl(),
                        "Created fileUrl is not the same."
                ),
                () -> assertEquals(
                        thumbnailUrl,
                        mediaFile.getThumbnailUrl(),
                        "Created thumbnailUrl is not the same."
                ),
                () -> assertEquals(
                        fileHash,
                        mediaFile.getFileHash(),
                        "Created fileHash is not the same."
                )
        );
    }

    @Test
    @DisplayName("Create MediaFile from invalid filename")
    void of2() {
        // Given invalid filename
        String filename = "example";
        // When builder
        var mediaFileBuilder = MediaFile.builder()
                .filename(filename)
                .extension(extension)
                .fileUrl(fileUrl)
                .thumbnailUrl(thumbnailUrl)
                .fileHash(fileHash);
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                mediaFileBuilder::build,
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Create MediaFile from invalid extension")
    void of3() {
        // Given not matching declared extension
        MediaExtension extension = MediaExtension.JPG;
        // When builder
        var mediaFileBuilder = MediaFile.builder()
                .filename(filename)
                .extension(extension)
                .fileUrl(fileUrl)
                .thumbnailUrl(thumbnailUrl)
                .fileHash(fileHash);
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                mediaFileBuilder::build,
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Create MediaFile from invalid files hash")
    void of4() {
        // Given invalid fileHash
        String fileHash = "test";
        // When builder
        var mediaFileBuilder = MediaFile.builder()
                .filename(filename)
                .extension(extension)
                .fileUrl(fileUrl)
                .thumbnailUrl(thumbnailUrl)
                .fileHash(fileHash);
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                mediaFileBuilder::build,
                "Exception was not thrown."
        );
    }
}