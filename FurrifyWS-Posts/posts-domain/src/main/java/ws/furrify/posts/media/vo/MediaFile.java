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
 * Media priority wrapper.
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

    @Builder
    private MediaFile(@NonNull final String filename,
                      @NonNull final MediaExtension extension,
                      final URL fileUrl,
                      final URL thumbnailUrl,
                      @NonNull final String fileHash) {
        if (filename.split(FILENAME_EXTENSION_DIVIDER).length < MINIMUM_DIVIDE_COUNT) {
            throw new IllegalStateException("Media filename [filename=" + filename + "] must contain extension.");
        }
        this.filename = filename;

        this.extension = extension;
        this.fileUrl = fileUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.fileHash = fileHash;
        this.status = MediaStatus.REQUEST_PENDING;
    }
}
