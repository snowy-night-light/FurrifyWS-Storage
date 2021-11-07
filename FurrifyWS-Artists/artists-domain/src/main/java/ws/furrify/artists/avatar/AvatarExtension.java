package ws.furrify.artists.avatar;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.FileUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Supported avatar extensions by system.
 *
 * @author Skyte
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum AvatarExtension {
    /**
     * File extensions
     */
    JPEG(MediaType.IMAGE, "image/jpeg"),
    PNG(MediaType.IMAGE, "image/png"),
    JPG(MediaType.IMAGE, "image/jpeg");

    /**
     * Mime type of extension.
     */
    private final String[] mimeTypes;

    /**
     * Avatar type ex. VIDEO, IMAGE.
     */
    private final MediaType type;

    private final static Pattern FILENAME_PATTERN = Pattern.compile("^[\\w,\\s-]+\\.[A-Za-z]{3}$");

    AvatarExtension(final MediaType type, final String... mimeTypes) {
        this.mimeTypes = mimeTypes;
        this.type = type;
    }

    public static boolean isValidFile(String filename,
                                      MultipartFile file,
                                      AvatarExtension avatarExtension) {
        try {
            // Check if filename matches regex for filename
            if (file.getOriginalFilename() == null ||
                    !FILENAME_PATTERN.matcher(file.getOriginalFilename()).matches()) {
                return false;
            }

            // Get file mimetype
            String mimeType = FileUtils.getMimeType(filename, file.getInputStream());

            return Arrays.asList(avatarExtension.getMimeTypes()).contains(mimeType);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Avatar type file represents.
     */
    private enum MediaType {
        /**
         * Image file type.
         */
        IMAGE
    }
}
