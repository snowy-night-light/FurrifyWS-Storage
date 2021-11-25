package ws.furrify.posts.media.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import ws.furrify.posts.media.MediaExtension;

import java.net.URI;

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

    @NonNull
    private URI fileUri;
    @NonNull
    private URI thumbnailUri;

    @NonNull
    private String md5;

    private final static String FILENAME_EXTENSION_DIVIDER = "\\.";
    private final static byte MINIMUM_DIVIDE_COUNT = 2;
    private final static String MD5_HASH_PATTERN = "[a-fA-F0-9]{32}";

    @Builder
    private MediaFile(@NonNull final String filename,
                      @NonNull final MediaExtension extension,
                      @NonNull final URI fileUri,
                      final URI thumbnailUri,
                      @NonNull final String md5) {

        // Validate given values
        String[] filenameWithExt = filename.split(FILENAME_EXTENSION_DIVIDER);

        // Check if valid filename
        if (filenameWithExt.length < MINIMUM_DIVIDE_COUNT) {
            throw new IllegalStateException("Media filename [filename=" + filename + "] must contain extension.");
        }

        // Check if filename extension matches declared
        if (!filenameWithExt[filenameWithExt.length - 1].equalsIgnoreCase(extension.name())) {
            throw new IllegalStateException("Media filename [filename=" + filename + "] must be the same as declared extension.");
        }

        // Check if MD5 hash is valid
        if (!md5.matches(MD5_HASH_PATTERN)) {
            throw new IllegalStateException("Media file hash [hash=" + md5 + "] is not valid MD5 hash.");
        }

        this.filename = filename;
        this.extension = extension;
        this.fileUri = fileUri;
        this.thumbnailUri = thumbnailUri;
        this.md5 = md5;
    }
}
