package ws.furrify.posts.media;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.FileUtils;

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
    OGG(MediaType.AUDIO, "application/ogg", "audio/ogg"),
    FLAC(MediaType.AUDIO, "audio/x-flac", "audio/flac"),
    MP3(MediaType.AUDIO, "audio/mpeg"),
    WAV(MediaType.AUDIO, "audio/wav", "audio/vnd.wave", "audio/wave", "audio/x-wav"),

    GIF(MediaType.ANIMATION, "image/gif"),

    TS(MediaType.VIDEO, "video/MP2T"),
    MOV(MediaType.VIDEO, "video/quicktime"),
    FLV(MediaType.VIDEO, "video/x-flv"),
    AVI(MediaType.VIDEO, "video/x-msvideo"),
    WMV(MediaType.VIDEO, "video/x-ms-wmv"),
    MKV(MediaType.VIDEO, "video/x-matroska"),
    WEBM(MediaType.VIDEO, "video/webm"),
    MP4(MediaType.VIDEO, "video/mp4"),
    MPEG(MediaType.IMAGE, "video/mpeg"),

    WEBP(MediaType.IMAGE, "image/webp"),
    ICO(MediaType.IMAGE, "image/vnd.microsoft.icon"),
    SVG(MediaType.IMAGE, "image/svg+xml"),
    TIF(MediaType.IMAGE, "image/tiff"),
    TIFF(MediaType.IMAGE, "image/tiff"),
    WBMP(MediaType.IMAGE, "image/vnd.wap.wbmp"),
    BMP(MediaType.IMAGE, "image/bmp"),
    BM(MediaType.IMAGE, "image/bmp"),
    PNG(MediaType.IMAGE, "image/png"),
    JPEG(MediaType.IMAGE, "image/jpeg"),
    JPG(MediaType.IMAGE, "image/jpeg");

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
            Set<String> allowedMimetypes = new HashSet<>(Arrays.asList(MediaExtension.JPG.getMimeTypes()));

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
