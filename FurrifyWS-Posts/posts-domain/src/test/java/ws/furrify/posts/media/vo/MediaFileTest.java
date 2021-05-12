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
    private static String md5;

    @BeforeAll
    static void setUp() throws MalformedURLException {
        filename = "test.png";
        extension = MediaExtension.PNG;
        fileUrl = new URL("https://google.pl");
        thumbnailUrl = new URL("https://google2.pl");
        md5 = "3c518eeb674c71b30297f072fde7eba5";
    }

    @Test
    @DisplayName("Create MediaFile")
    void of() {
        // Given filename, extension, fileUrl, thumbnailUrl and md5
        // When builder build
        var mediaFile = MediaFile.builder()
                .filename(filename)
                .extension(extension)
                .fileUrl(fileUrl)
                .thumbnailUrl(thumbnailUrl)
                .md5(md5)
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
                        md5,
                        mediaFile.getMd5(),
                        "Created md5 is not the same."
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
                .md5(md5);
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
                .md5(md5);
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
        // Given invalid md5
        String md5 = "test";
        // When builder
        var mediaFileBuilder = MediaFile.builder()
                .filename(filename)
                .extension(extension)
                .fileUrl(fileUrl)
                .thumbnailUrl(thumbnailUrl)
                .md5(md5);
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                mediaFileBuilder::build,
                "Exception was not thrown."
        );
    }
}