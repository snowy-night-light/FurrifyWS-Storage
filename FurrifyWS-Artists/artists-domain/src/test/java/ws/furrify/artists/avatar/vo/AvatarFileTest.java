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
    private static URI fileUrl;
    private static URI thumbnailUrl;
    private static String md5;

    @BeforeAll
    static void setUp() throws URISyntaxException {
        filename = "test.png";
        extension = AvatarExtension.PNG;
        fileUrl = new URI("/test");
        thumbnailUrl = new URI("/test");
        md5 = "3c518eeb674c71b30297f072fde7eba5";
    }

    @Test
    @DisplayName("Create AvatarFile")
    void of() {
        // Given filename, extension, fileUrl, thumbnailUrl and md5
        // When builder build
        var avatarFile = AvatarFile.builder()
                .filename(filename)
                .extension(extension)
                .fileUrl(fileUrl)
                .thumbnailUrl(thumbnailUrl)
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
                        fileUrl,
                        avatarFile.getFileUrl(),
                        "Created fileUrl is not the same."
                ),
                () -> assertEquals(
                        thumbnailUrl,
                        avatarFile.getThumbnailUrl(),
                        "Created thumbnailUrl is not the same."
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
                .fileUrl(fileUrl)
                .thumbnailUrl(thumbnailUrl)
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
        AvatarExtension extension = AvatarExtension.JPG;
        // When builder
        var avatarFileBuilder = AvatarFile.builder()
                .filename(filename)
                .extension(extension)
                .fileUrl(fileUrl)
                .thumbnailUrl(thumbnailUrl)
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
                .fileUrl(fileUrl)
                .thumbnailUrl(thumbnailUrl)
                .md5(md5);
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                avatarFileBuilder::build,
                "Exception was not thrown."
        );
    }
}