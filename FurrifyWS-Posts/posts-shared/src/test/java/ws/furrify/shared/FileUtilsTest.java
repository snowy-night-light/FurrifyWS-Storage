package ws.furrify.shared;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import ws.furrify.posts.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

class FileUtilsTest {
    @DisplayName("Test mime type detector")
    @ParameterizedTest
    @CsvFileSource(resources = "mimeTypes.csv", numLinesToSkip = 1)
    void getMimeType(String filePath, String mimeType) throws IOException {
        // Given filePath and valid mimeType
        String filename = new File(filePath).getName();
        InputStream fileIn = getClass().getClassLoader().getResourceAsStream(filePath);
        // When getMimeType()
        // Then
        Assertions.assertEquals(
                mimeType,
                FileUtils.getMimeType(filename, fileIn),
                "Mime type is not detected correctly."
        );
    }
}