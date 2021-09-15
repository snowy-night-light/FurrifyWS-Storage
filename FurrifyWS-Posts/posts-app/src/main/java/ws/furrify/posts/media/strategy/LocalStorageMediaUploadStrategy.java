package ws.furrify.posts.media.strategy;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.FileContentIsCorruptedException;
import ws.furrify.shared.exception.FileUploadCannotCreatePathException;
import ws.furrify.shared.exception.FileUploadFailedException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.UUID;

/**
 * Upload media file to local storage strategy.
 * Needs to be created as bean for @Value to work.
 *
 * @author sky
 */
@RequiredArgsConstructor
public class LocalStorageMediaUploadStrategy implements MediaUploadStrategy {

    @Value("${LOCAL_STORAGE_MEDIA_PATH:/data/media}")
    private String LOCAL_STORAGE_MEDIA_PATH;

    @Value("${THUMBNAIL_WIDTH:600}")
    private int THUMBNAIL_WIDTH;

    @Value("${THUMBNAIL_QUALITY:0.90}")
    private float THUMBNAIL_QUALITY;

    @Value("${THUMBNAIL_PREFIX:thumbnail_}")
    private String THUMBNAIL_PREFIX;

    // FIXME Url should update on all records when changed
    @Value("${LOCAL_STORAGE_MEDIA_URL:https://localhost}")
    private String LOCAL_STORAGE_MEDIA_URL;

    private final static String THUMBNAIL_EXTENSION = ".jpg";

    @Override
    public UploadedMediaFile uploadMediaWithGeneratedThumbnail(final UUID mediaId, final MultipartFile fileSource) {
        try (
                // Generate thumbnail
                InputStream thumbnailInputStream = MediaUploadStrategyUtils.generateThumbnail(
                        THUMBNAIL_WIDTH,
                        THUMBNAIL_QUALITY,
                        fileSource.getInputStream()
                );
                InputStream mediaInputStream = fileSource.getInputStream()
        ) {

            // Create files
            File mediaFile = new File(LOCAL_STORAGE_MEDIA_PATH + "/" + mediaId + "/" + fileSource.getOriginalFilename());

            // Create thumbnail filename by removing extension from original filename
            // FIXME May be null
            String thumbnailFileName = fileSource.getOriginalFilename().substring(
                    0,
                    fileSource.getOriginalFilename().lastIndexOf(".")
            ) + THUMBNAIL_EXTENSION;

            File thumbnailFile = new File(LOCAL_STORAGE_MEDIA_PATH + "/" + mediaId + "/" + THUMBNAIL_PREFIX + thumbnailFileName);
            // Create directories where files need to be located
            boolean wasMediaFileFolderCreated = mediaFile.getParentFile().mkdirs() || mediaFile.getParentFile().exists();
            boolean wasMediaThumbnailFolderCreated = thumbnailFile.getParentFile().mkdirs() || mediaFile.getParentFile().exists();

            if (!wasMediaFileFolderCreated || !wasMediaThumbnailFolderCreated) {
                throw new FileUploadCannotCreatePathException(Errors.FILE_UPLOAD_CANNOT_CREATE_PATH.getErrorMessage());
            }

            // Upload files
            writeToFile(mediaFile, mediaInputStream);
            writeToFile(thumbnailFile, thumbnailInputStream);

            // Return created urls
            return new UploadedMediaFile(
                    new URL(LOCAL_STORAGE_MEDIA_URL + mediaFile.getPath()),
                    new URL(LOCAL_STORAGE_MEDIA_URL + thumbnailFile.getPath())
            );

        } catch (IOException e) {
            throw new FileContentIsCorruptedException(Errors.FILE_CONTENT_IS_CORRUPTED.getErrorMessage());
        }
    }


    private void writeToFile(File file, InputStream inputStream) {
        try (OutputStream outputStream = new FileOutputStream(file)) {
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            throw new FileUploadFailedException(Errors.FILE_UPLOAD_FAILED.getErrorMessage());
        }
    }

}
