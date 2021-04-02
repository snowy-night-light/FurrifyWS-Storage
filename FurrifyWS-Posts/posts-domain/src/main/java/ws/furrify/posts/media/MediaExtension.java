package ws.furrify.posts.media;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ws.furrify.posts.FileUtils;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.FileContentIsCorruptedException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Supported media extensions by system.
 *
 * @author Skyte
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum MediaExtension {
    /**
     * File extensions
     */
    JPEG("image/jpeg", MediaType.IMAGE),
    PNG("image/png", MediaType.IMAGE),
    JPG("image/jpeg", MediaType.IMAGE);

    /**
     * Mime type of extension.
     */
    private final String mimeType;

    /**
     * Media type ex. VIDEO, IMAGE.
     */
    private final MediaType type;

    public static boolean isValidFile(String filename,
                                      InputStream inputStream,
                                      MediaExtension mediaExtension) {
        try {
            String mimeType = FileUtils.getMimeType(filename, inputStream);

            return mimeType.equals(mediaExtension.getMimeType());
        } catch (IOException e) {
            throw new FileContentIsCorruptedException(Errors.FILE_CONTENT_IS_CORRUPTED.getErrorMessage());
        }
    }

    /**
     * Media type file represents.
     */
    private enum MediaType {
        /**
         * Image file type.
         */
        IMAGE
    }
}
