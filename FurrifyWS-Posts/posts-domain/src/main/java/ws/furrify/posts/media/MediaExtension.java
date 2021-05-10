package ws.furrify.posts.media;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.FileUtils;

import java.io.IOException;

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
                                      MultipartFile file,
                                      MediaExtension mediaExtension) {
        try {
            String mimeType = FileUtils.getMimeType(filename, file.getInputStream());

            return mimeType.equals(mediaExtension.getMimeType());
        } catch (IOException e) {
            return false;
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
