package ws.furrify.posts.media.strategy;

import net.coobird.thumbnailator.Thumbnails;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import ws.furrify.posts.media.MediaExtension;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.FileContentIsCorruptedException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utils class for media upload strategy.
 *
 * @author sky
 */
public class MediaUploadStrategyUtils {

    private final static int PART_OF_VIDEO_TO_THUMBNAIL = 3;

    public static InputStream generateThumbnail(final MediaExtension.MediaType mediaType,
                                                final int width,
                                                final float quality,
                                                final InputStream source) throws IOException {
        return switch (mediaType) {
            case IMAGE -> generateThumbnailForImage(width, quality, source);
            case VIDEO -> generateThumbnailForImage(
                    width,
                    quality,
                    extractFrameForVideo(source)
            );
        };
    }

    private static InputStream generateThumbnailForImage(final int width,
                                                         final float quality,
                                                         final InputStream source) throws IOException {
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

    private static InputStream extractFrameForVideo(final InputStream source) {

        try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(source)) {
            frameGrabber.start();
            frameGrabber.setFrameNumber(frameGrabber.getLengthInFrames() / PART_OF_VIDEO_TO_THUMBNAIL);

            Java2DFrameConverter converter = new Java2DFrameConverter();

            Frame f = frameGrabber.grabImage();
            BufferedImage image = converter.convert(f);

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image, "png", output);

            frameGrabber.stop();

            return new ByteArrayInputStream(output.toByteArray());
        } catch (IOException e) {
            throw new FileContentIsCorruptedException(Errors.FILE_CONTENT_IS_CORRUPTED.getErrorMessage());
        }
    }

}
