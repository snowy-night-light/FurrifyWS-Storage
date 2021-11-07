package ws.furrify.artists.avatar.strategy;

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
 * Upload avatar file to local storage strategy.
 * Needs to be created as bean for @Value to work.
 *
 * @author sky
 */
@RequiredArgsConstructor
public class LocalStorageAvatarUploadStrategy implements AvatarUploadStrategy {

    @Value("${LOCAL_STORAGE_AVATAR_PATH:/data/artist}")
    private String LOCAL_STORAGE_AVATAR_PATH;

    @Value("${REMOTE_STORAGE_AVATAR_PATH:/artist/")
    private String REMOTE_STORAGE_AVATAR_PATH;

    @Value("${THUMBNAIL_WIDTH:600}")
    private int THUMBNAIL_WIDTH;

    @Value("${THUMBNAIL_QUALITY:0.90}")
    private float THUMBNAIL_QUALITY;

    @Value("${THUMBNAIL_PREFIX:thumbnail_}")
    private String THUMBNAIL_PREFIX;

    // FIXME Url should update on all records when changed
    @Value("${REMOTE_STORAGE_AVATAR_URL:http://localhost}")
    private String REMOTE_STORAGE_AVATAR_URL;

    private final static String THUMBNAIL_EXTENSION = ".jpg";

    @Override
    public UploadedAvatarFile uploadAvatarWithGeneratedThumbnail(final UUID artistId, final UUID avatarId, final MultipartFile fileSource) {
        try (
                // Generate thumbnail
                InputStream thumbnailInputStream = AvatarUploadStrategyUtils.generateThumbnail(
                        THUMBNAIL_WIDTH,
                        THUMBNAIL_QUALITY,
                        fileSource.getInputStream()
                );
                InputStream avatarInputStream = fileSource.getInputStream()
        ) {

            // Create files
            File avatarFile = new File(LOCAL_STORAGE_AVATAR_PATH + "/" + artistId + "/" + avatarId + "/" + fileSource.getOriginalFilename());

            // Check if filename is not null
            if (fileSource.getOriginalFilename() == null) {
                throw new IllegalStateException("Filename cannot be empty.");
            }

            // Create thumbnail filename by removing extension from original filename
            String thumbnailFileName = THUMBNAIL_PREFIX +
                    fileSource.getOriginalFilename().substring(
                            0,
                            fileSource.getOriginalFilename().lastIndexOf(".")
                    ) + THUMBNAIL_EXTENSION;

            File thumbnailFile = new File(LOCAL_STORAGE_AVATAR_PATH + "/" + artistId + "/" + avatarId + "/" + thumbnailFileName);
            // Create directories where files need to be located
            boolean wasAvatarFileFolderCreated = avatarFile.getParentFile().mkdirs() || avatarFile.getParentFile().exists();
            boolean wasAvatarThumbnailFolderCreated = thumbnailFile.getParentFile().mkdirs() || avatarFile.getParentFile().exists();

            if (!wasAvatarFileFolderCreated || !wasAvatarThumbnailFolderCreated) {
                throw new FileUploadCannotCreatePathException(Errors.FILE_UPLOAD_CANNOT_CREATE_PATH.getErrorMessage());
            }

            // Upload files
            writeToFile(avatarFile, avatarInputStream);
            writeToFile(thumbnailFile, thumbnailInputStream);

            // Return created urls
            return new UploadedAvatarFile(
                    // Original
                    new URL(REMOTE_STORAGE_AVATAR_URL + REMOTE_STORAGE_AVATAR_PATH + "/" + artistId + "/" + avatarId + "/" + fileSource.getOriginalFilename()),
                    // Thumbnail
                    new URL(REMOTE_STORAGE_AVATAR_URL + REMOTE_STORAGE_AVATAR_PATH + "/" + artistId + "/" + avatarId + "/" + thumbnailFileName)
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
