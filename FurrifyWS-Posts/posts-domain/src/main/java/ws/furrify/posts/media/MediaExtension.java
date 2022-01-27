package ws.furrify.posts.media;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.utils.FileUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

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
    EXTENSION_OGG(MediaType.AUDIO, "application/ogg", "audio/ogg"),
    EXTENSION_FLAC(MediaType.AUDIO, "audio/x-flac", "audio/flac"),
    EXTENSION_MP3(MediaType.AUDIO, "audio/mpeg"),
    EXTENSION_WAV(MediaType.AUDIO, "audio/wav", "audio/vnd.wave", "audio/wave", "audio/x-wav"),

    EXTENSION_GIF(MediaType.ANIMATION, "image/gif"),

    EXTENSION_TS(MediaType.VIDEO, "video/MP2T"),
    EXTENSION_MOV(MediaType.VIDEO, "video/quicktime"),
    EXTENSION_FLV(MediaType.VIDEO, "video/x-flv"),
    EXTENSION_AVI(MediaType.VIDEO, "video/x-msvideo"),
    EXTENSION_WMV(MediaType.VIDEO, "video/x-ms-wmv"),
    EXTENSION_MKV(MediaType.VIDEO, "video/x-matroska"),
    EXTENSION_WEBM(MediaType.VIDEO, "video/webm"),
    EXTENSION_MP4(MediaType.VIDEO, "video/mp4"),
    EXTENSION_MPEG(MediaType.IMAGE, "video/mpeg"),

    EXTENSION_WEBP(MediaType.IMAGE, "image/webp"),
    EXTENSION_ICO(MediaType.IMAGE, "image/vnd.microsoft.icon"),
    EXTENSION_SVG(MediaType.IMAGE, "image/svg+xml"),
    EXTENSION_TIF(MediaType.IMAGE, "image/tiff"),
    EXTENSION_TIFF(MediaType.IMAGE, "image/tiff"),
    EXTENSION_WBMP(MediaType.IMAGE, "image/vnd.wap.wbmp"),
    EXTENSION_BMP(MediaType.IMAGE, "image/bmp"),
    EXTENSION_BM(MediaType.IMAGE, "image/bmp"),
    EXTENSION_PNG(MediaType.IMAGE, "image/png"),
    EXTENSION_JPEG(MediaType.IMAGE, "image/jpeg"),
    EXTENSION_JPG(MediaType.IMAGE, "image/jpeg");

    /**
     * Mime type of extension.
     */
    private final String[] mimeTypes;

    /**
     * Media type ex. VIDEO, IMAGE.
     */
    private final MediaType type;

    private final static Pattern FILENAME_PATTERN = Pattern.compile("^[a-zA-Z0-9](?:[a-zA-Z0-9() ._-]*[a-zA-Z0-9() ._-])?\\.[a-zA-Z0-9_-]+$");

    MediaExtension(final MediaType type, final String... mimeTypes) {
        this.mimeTypes = mimeTypes;
        this.type = type;
    }

    public static boolean isFileContentValid(String filename,
                                             MultipartFile file,
                                             MediaExtension mediaExtension) {
        try {
            // Get file mimetype
            String mimeType = FileUtils.getMimeType(filename, file.getInputStream());

            return Arrays.asList(mediaExtension.getMimeTypes()).contains(mimeType);
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isThumbnailValid(final String filename,
                                           final MultipartFile file) {
        try {
            // Get file mimetype
            String mimeType = FileUtils.getMimeType(filename, file.getInputStream());

            // Add allowed mime types to set
            Set<String> allowedMimetypes = new HashSet<>(Arrays.asList(MediaExtension.EXTENSION_JPG.getMimeTypes()));

            return allowedMimetypes.contains(mimeType);
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isFilenameValid(String filename) {
        return filename != null && FILENAME_PATTERN.matcher(filename).matches();
    }


    /**
     * Media type file represents.
     */
    public enum MediaType {
        /**
         * Image file type.
         */
        IMAGE,
        /**
         * Video file type.
         */
        VIDEO,
        /**
         * Animation file type.
         */
        ANIMATION,
        /**
         * Audio file type.
         */
        AUDIO
    }
}
