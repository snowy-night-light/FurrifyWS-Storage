package ws.furrify.posts.attachment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.FileUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Supported attachment extensions by system.
 *
 * @author Skyte
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum AttachmentExtension {
    /**
     * File extensions
     */
    SWF(AttachmentType.ADOBE_FLASH, "application/x-shockwave-flash"),
    PSD(AttachmentType.PHOTOSHOP_DOCUMENT, "image/vnd.adobe.photoshop"),
    BLEND(AttachmentType.BLENDER_PROJECT, "application/octet-stream");

    /**
     * Mime type of extension.
     */
    private final String[] mimeTypes;

    /**
     * Attachment type ex. PHOTOSHOP_DOCUMENT.
     */
    private final AttachmentType type;

    private final static Pattern FILENAME_PATTERN = Pattern.compile("^[\\w,\\s-]+\\.[A-Za-z1-9]{3,4}$");

    AttachmentExtension(final AttachmentType type, final String... mimeTypes) {
        this.mimeTypes = mimeTypes;
        this.type = type;
    }

    public static boolean isFileContentValid(String filename,
                                             MultipartFile file,
                                             AttachmentExtension attachmentExtension) {
        try {
            // Get file mimetype
            String mimeType = FileUtils.getMimeType(filename, file.getInputStream());

            return Arrays.asList(attachmentExtension.getMimeTypes()).contains(mimeType);
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isFilenameValid(String filename) {
        return !(filename == null || !FILENAME_PATTERN.matcher(filename).matches());
    }

    /**
     * Attachment type file represents.
     */
    private enum AttachmentType {
        /**
         * Photoshop Document
         */
        PHOTOSHOP_DOCUMENT,
        /**
         * Blender project file
         */
        BLENDER_PROJECT,
        /**
         * Adobe flash file
         */
        ADOBE_FLASH
    }
}
