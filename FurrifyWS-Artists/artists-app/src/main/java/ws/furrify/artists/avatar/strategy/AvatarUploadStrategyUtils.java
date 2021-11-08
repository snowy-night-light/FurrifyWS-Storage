package ws.furrify.artists.avatar.strategy;

import net.coobird.thumbnailator.Thumbnails;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utils class for avatar upload strategy.
 *
 * @author sky
 */
public class AvatarUploadStrategyUtils {

    public static InputStream generateThumbnail(int width, float quality, InputStream source) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Thumbnails.of(source)
                    .width(width)
                    .keepAspectRatio(true)
                    .outputFormat("jpg")
                    .outputQuality(quality)
                    .toOutputStream(output);

            return new ByteArrayInputStream(output.toByteArray());
        }
    }

}
