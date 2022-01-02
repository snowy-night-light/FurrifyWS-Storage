package ws.furrify.posts.attachment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.utils.FileUtils;

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
    MID(AttachmentType.MUSIC_DIGITAL_INTERFACE, "application/vnd.microsoft.portable-executable"),
    MIDI(AttachmentType.MUSIC_DIGITAL_INTERFACE, "application/java-archive"),

    EXE(AttachmentType.PROGRAM, "application/vnd.microsoft.portable-executable"),
    JAR(AttachmentType.PROGRAM, "application/java-archive"),

    PPT(AttachmentType.PRESENTATION, "application/vnd.ms-powerpoint"),
    PPTX(AttachmentType.PRESENTATION, "application/vnd.openxmlformats-officedocument.presentationml.presentation"),

    ODT(AttachmentType.TEXT_DOCUMENT, "application/vnd.oasis.opendocument.text"),
    TXT(AttachmentType.TEXT_DOCUMENT, "text/plain"),
    DOC(AttachmentType.TEXT_DOCUMENT, "application/msword"),
    DOCX(AttachmentType.TEXT_DOCUMENT, "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),

    TORRENT(AttachmentType.TORRENT, "application/x-bittorrent"),

    AZW3(AttachmentType.EBOOK, "application/vnd.amazon.mobi8-ebook"),
    MOBI(AttachmentType.EBOOK, "application/x-mobipocket-ebook"),
    EPUB(AttachmentType.EBOOK, "application/epub+zip"),

    PDF(AttachmentType.ADOBE_DOCUMENT, "application/pdf"),

    SAI(AttachmentType.PAINTTOOL_SAI_DOCUMENT, "application/octet-stream"),

    ZIP(AttachmentType.COMPRESSED_ARCHIVE, "application/zip"),
    RAR(AttachmentType.COMPRESSED_ARCHIVE, "application/vnd.rar"),
    BZIP(AttachmentType.COMPRESSED_ARCHIVE, "application/x-bzip"),
    BZIP2(AttachmentType.COMPRESSED_ARCHIVE, "application/x-bzip2"),

    TAR(AttachmentType.ARCHIVE, "application/x-tar"),

    GZ(AttachmentType.COMPRESSED, "application/gzip"),

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

    private final static Pattern FILENAME_PATTERN = Pattern.compile("^[a-zA-Z0-9](?:[a-zA-Z0-9() ._-]*[a-zA-Z0-9() ._-])?\\.[a-zA-Z0-9_-]+$");

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
         * Presentation file
         */
        PRESENTATION,
        /**
         * Musical Instrument Digital Interface (MIDI)
         */
        MUSIC_DIGITAL_INTERFACE,
        /**
         * Program file
         */
        PROGRAM,
        /**
         * Torrent file
         */
        TORRENT,
        /**
         * Text document
         */
        TEXT_DOCUMENT,
        /**
         * eBook format
         */
        EBOOK,
        /**
         * PDF document
         */
        ADOBE_DOCUMENT,
        /**
         * PaintTool SAI document
         */
        PAINTTOOL_SAI_DOCUMENT,
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
        ADOBE_FLASH,
        /**
         * Compressed archive file
         */
        COMPRESSED_ARCHIVE,
        /**
         * Archive file
         */
        ARCHIVE,
        /**
         * Compressed file
         */
        COMPRESSED
    }
}
