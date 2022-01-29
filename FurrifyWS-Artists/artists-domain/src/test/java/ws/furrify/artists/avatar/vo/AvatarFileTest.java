package ws.furrify.artists.avatar.vo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.artists.avatar.AvatarExtension;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AvatarFileTest {

    private static String filename;
    private static AvatarExtension extension;
    private static URI fileUri;
    private static URI thumbnailUri;
    private static String md5;

    @BeforeAll
    static void setUp() throws URISyntaxException {
        filename = "test.png";
        extension = AvatarExtension.EXTENSION_PNG;
        fileUri = new URI("/test");
        thumbnailUri = new URI("/test");
        md5 = "3c518eeb674c71b30297f072fde7eba5";
    }

    @Test
    @DisplayName("Create AvatarFile")
    void of() {
        // Given filename, extension, fileUri, thumbnailUri and md5
        // When builder build
        var avatarFile = AvatarFile.builder()
                .filename(filename)
                .extension(extension)
                .fileUri(fileUri)
                .thumbnailUri(thumbnailUri)
                .md5(md5)
                .build();
        // Then return created AvatarFile
        assertAll(
                () -> assertEquals(
                        filename,
                        avatarFile.getFilename(),
                        "Created filename is not the same."
                ),
                () -> assertEquals(
                        extension,
                        avatarFile.getExtension(),
                        "Created extension is not the same."
                ),
                () -> assertEquals(
                        fileUri,
                        avatarFile.getFileUri(),
                        "Created fileUri is not the same."
                ),
                () -> assertEquals(
                        thumbnailUri,
                        avatarFile.getThumbnailUri(),
                        "Created thumbnailUri is not the same."
                ),
                () -> assertEquals(
                        md5,
                        avatarFile.getMd5(),
                        "Created md5 is not the same."
                )
        );
    }

    @Test
    @DisplayName("Create AvatarFile from invalid filename")
    void of2() {
        // Given invalid filename
        String filename = "example";
        // When builder
        var avatarFileBuilder = AvatarFile.builder()
                .filename(filename)
                .extension(extension)
                .fileUri(fileUri)
                .thumbnailUri(thumbnailUri)
                .md5(md5);
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                avatarFileBuilder::build,
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Create AvatarFile from invalid extension")
    void of3() {
        // Given not matching declared extension
        AvatarExtension extension = AvatarExtension.EXTENSION_JPG;
        // When builder
        var avatarFileBuilder = AvatarFile.builder()
                .filename(filename)
                .extension(extension)
                .fileUri(fileUri)
                .thumbnailUri(thumbnailUri)
                .md5(md5);
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                avatarFileBuilder::build,
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Create AvatarFile from invalid files hash")
    void of4() {
        // Given invalid md5
        String md5 = "test";
        // When builder
        var avatarFileBuilder = AvatarFile.builder()
                .filename(filename)
                .extension(extension)
                .fileUri(fileUri)
                .thumbnailUri(thumbnailUri)
                .md5(md5);
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                avatarFileBuilder::build,
                "Exception was not thrown."
        );
    }
}