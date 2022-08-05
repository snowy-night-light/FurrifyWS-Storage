package ws.furrify.artists.avatar.strategy;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.FileContentIsCorruptedException;
import ws.furrify.shared.exception.FileUploadCannotCreatePathException;
import ws.furrify.shared.exception.FileUploadFailedException;
import ws.furrify.shared.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Upload avatar file to local storage strategy.
 * Needs to be created as bean for @Value to work.
 *
 * @author sky
 */
@RequiredArgsConstructor
@Log4j2
public class LocalStorageAvatarUploadStrategy implements AvatarUploadStrategy {

    private final static String AVATAR_DIRECTORY = "file";
    private final static String THUMBNAIL_DIRECTORY = "thumbnail";

    @Value("${THUMBNAIL_WIDTH:600}")
    private int THUMBNAIL_WIDTH;

    @Value("${THUMBNAIL_QUALITY:0.90}")
    private float THUMBNAIL_QUALITY;

    @Value("${THUMBNAIL_PREFIX:thumbnail_}")
    private String THUMBNAIL_PREFIX;
    @Value("${LOCAL_STORAGE_AVATAR_PATH:/data/avatar}")
    private String LOCAL_STORAGE_AVATAR_PATH;
    @Value("${REMOTE_STORAGE_AVATAR_PATH:/avatar}")
    private String REMOTE_STORAGE_AVATAR_PATH;

    private final static String THUMBNAIL_EXTENSION = ".jpg";

    @Override
    public UploadedAvatarFile uploadAvatarWithGeneratedThumbnail(final UUID avatarId, final MultipartFile fileSource) {
        try (
                // Generate thumbnail
                InputStream thumbnailInputStream = AvatarUploadStrategyUtils.generateThumbnail(
                        THUMBNAIL_WIDTH,
                        THUMBNAIL_QUALITY,
                        fileSource.getInputStream()
                );
                InputStream avatarInputStream = fileSource.getInputStream()
        ) {

            // Check if filename is not null
            if (fileSource.getOriginalFilename() == null) {
                throw new IllegalStateException("Filename cannot be empty.");
            }

            // Remove all files before uploading new ones
            removeAllAvatarFiles(avatarId);

            // Sanitize filename
            String filename = fileSource.getOriginalFilename().replaceAll("\\s+", "_");

            // Create files
            File avatarFile = new File(LOCAL_STORAGE_AVATAR_PATH + "/" + avatarId + "/" + AVATAR_DIRECTORY + "/" + filename);

            // Create thumbnail filename by removing extension from original filename
            String thumbnailFilename = THUMBNAIL_PREFIX +
                    filename.substring(
                            0,
                            filename.lastIndexOf(".")
                    ) + THUMBNAIL_EXTENSION;

            File thumbnailFile = new File(LOCAL_STORAGE_AVATAR_PATH + "/" + avatarId + "/" + THUMBNAIL_DIRECTORY + "/" + thumbnailFilename);
            // Create directories where files need to be located
            boolean wasAvatarFileCreated = avatarFile.getParentFile().mkdirs() || avatarFile.getParentFile().exists();
            boolean wasAvatarThumbnailFileCreated = thumbnailFile.getParentFile().mkdirs() || avatarFile.getParentFile().exists();

            if (!wasAvatarFileCreated || !wasAvatarThumbnailFileCreated) {
                throw new FileUploadCannotCreatePathException(Errors.FILE_UPLOAD_CANNOT_CREATE_PATH.getErrorMessage());
            }

            // Upload files
            writeToFile(avatarFile, avatarInputStream);
            writeToFile(thumbnailFile, thumbnailInputStream);

            // Return created urls
            return new UploadedAvatarFile(
                    // Original
                    new URI(REMOTE_STORAGE_AVATAR_PATH + "/" + avatarId + "/" + THUMBNAIL_DIRECTORY + "/" + filename),
                    // Thumbnail
                    new URI(REMOTE_STORAGE_AVATAR_PATH + "/" + avatarId + "/" + THUMBNAIL_DIRECTORY + "/" + thumbnailFilename)
            );

        } catch (IOException | URISyntaxException e) {
            throw new FileContentIsCorruptedException(Errors.FILE_CONTENT_IS_CORRUPTED.getErrorMessage());
        }
    }

    @Override
    public void removeAllAvatarFiles(@NonNull final UUID avatarId) {
        File mediaDir = new java.io.File(LOCAL_STORAGE_AVATAR_PATH + "/" + avatarId);

        if (mediaDir.exists()) {
            FileUtils.deleteDirectoryWithFiles(mediaDir);
        } else {
            log.error("Attempting to remove not existing directory [path=" + mediaDir.getAbsolutePath() + "].");
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
