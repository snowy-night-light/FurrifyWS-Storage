package ws.furrify.posts.media.strategy;

import net.coobird.thumbnailator.Thumbnails;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import ws.furrify.posts.media.MediaExtension;
import ws.furrify.posts.utils.GifDecoder;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.FileContentIsCorruptedException;
import ws.furrify.shared.exception.VideoFrameExtractionFailedException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static ws.furrify.posts.media.MediaExtension.GIF;

/**
 * Utils class for media upload strategy.
 *
 * @author sky
 */
public class MediaUploadStrategyUtils {

    private final static int PART_OF_VIDEO_TO_THUMBNAIL = 3;
    private final static int TIMOUT_FRAME_EXTRACTION_SECONDS = 10;

    public static InputStream generateThumbnail(final MediaExtension extension,
                                                final int width,
                                                final float quality,
                                                final InputStream source) throws IOException {
        return switch (extension.getType()) {
            case IMAGE -> generateThumbnailForImage(width, quality, source);
            case VIDEO -> generateThumbnailForImage(
                    width,
                    quality,
                    extractFrameForVideo(source)
            );
            case ANIMATION -> {
                // Workaround for gif
                if (extension == GIF) {
                    InputStream frame = extractFirstFrameFromGif(source);

                    yield generateThumbnailForImage(width, quality, frame);
                }

                yield null;
            }
            case AUDIO -> null;
        };
    }

    private static InputStream extractFirstFrameFromGif(final InputStream source) throws IOException {
        /*
            TODO Fix gif when JDK is fixed
            Current JDK Gif decoder is broken. It only allows 4000 ish frames. Included workaround below.
         */
        GifDecoder.GifImage gifImage = GifDecoder.read(source);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(gifImage.getFrame(0), "png", output);

        return new ByteArrayInputStream(output.toByteArray());
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

        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 1, TIMOUT_FRAME_EXTRACTION_SECONDS, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        Future<InputStream> inputStreamFuture = threadPool.submit(() -> {

            try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(source)) {
                frameGrabber.start();

                Java2DFrameConverter converter = new Java2DFrameConverter();
                frameGrabber.setVideoFrameNumber(frameGrabber.getLengthInFrames() / PART_OF_VIDEO_TO_THUMBNAIL);

                Frame frame = frameGrabber.grabImage();
                BufferedImage image = converter.convert(frame);

                ByteArrayOutputStream output = new ByteArrayOutputStream();
                ImageIO.write(image, "png", output);

                frameGrabber.stop();

                return new ByteArrayInputStream(output.toByteArray());
            } catch (IOException e) {
                throw new FileContentIsCorruptedException(Errors.FILE_CONTENT_IS_CORRUPTED.getErrorMessage());
            }
        });

        try {
            return inputStreamFuture.get(TIMOUT_FRAME_EXTRACTION_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            // Interrupt task
            inputStreamFuture.cancel(true);

            throw new VideoFrameExtractionFailedException(Errors.VIDEO_FRAME_EXTRACTION_FAILED.getErrorMessage());
        }
    }

}
