package ws.furrify.posts.media.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import ws.furrify.posts.media.MediaExtension;
import ws.furrify.posts.media.MediaStatus;

import java.net.URL;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

/**
 * Media file wrapper.
 *
 * @author Skyte
 */
@Data
@Setter(value = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class MediaFile {

    @NonNull
    private String filename;
    @NonNull
    private MediaExtension extension;

    private URL fileUrl;
    private URL thumbnailUrl;

    @NonNull
    private String fileHash;
    @NonNull
    private MediaStatus status;

    private final static String FILENAME_EXTENSION_DIVIDER = "\\.";
    private final static byte MINIMUM_DIVIDE_COUNT = 2;
    private final static String MD5_HASH_PATTERN = "[a-fA-F0-9]{32}";

    @Builder
    private MediaFile(@NonNull final String filename,
                      @NonNull final MediaExtension extension,
                      final URL fileUrl,
                      final URL thumbnailUrl,
                      @NonNull final String fileHash) {

        // Validate given values
        String[] filenameWithExt = filename.split(FILENAME_EXTENSION_DIVIDER);

        // Check if valid filename
        if (filenameWithExt.length < MINIMUM_DIVIDE_COUNT) {
            throw new IllegalStateException("Media filename [filename=" + filename + "] must contain extension.");
        }

        // Check if filename extension matches declared
        if (!filenameWithExt[1].equalsIgnoreCase(extension.name())) {
            throw new IllegalStateException("Media filename [filename=" + filename + "] must be the same as declared extension.");
        }

        // Check if MD5 hash is valid
        if (!fileHash.matches(MD5_HASH_PATTERN)) {
            throw new IllegalStateException("Media file hash [hash=" + fileHash + "] is not valid MD5 hash.");
        }

        this.filename = filename;
        this.extension = extension;
        this.fileUrl = fileUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.fileHash = fileHash;
        this.status = MediaStatus.REQUEST_PENDING;
    }
}
