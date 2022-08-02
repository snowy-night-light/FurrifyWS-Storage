package ws.furrify.posts.media;

import lombok.NonNull;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.media.dto.MediaDTO;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.FileContentIsCorruptedException;
import ws.furrify.shared.exception.FileExtensionIsNotMatchingContentException;
import ws.furrify.shared.exception.FilenameIsInvalidException;
import ws.furrify.shared.exception.RecordAlreadyExistsException;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * Utils class regarding Media files.
 *
 * @author Skyte
 */
class FileUtils {

    public static String generateMd5FromFile(@NonNull final MultipartFile mediaFile) {
        try {
            // Get file hash
            return DigestUtils.md5Hex(mediaFile.getInputStream());
        } catch (IOException e) {
            throw new FileContentIsCorruptedException(Errors.FILE_CONTENT_IS_CORRUPTED.getErrorMessage());
        }
    }

    public static void validateThumbnail(@NonNull final MultipartFile thumbnailFile) throws FileExtensionIsNotMatchingContentException {
        // Check if thumbnail meets the requirements
        boolean isThumbnailFileValid = MediaExtension.isThumbnailValid(
                thumbnailFile.getOriginalFilename(),
                thumbnailFile
        );
        if (!isThumbnailFileValid) {
            throw new FileExtensionIsNotMatchingContentException(Errors.THUMBNAIL_CONTENT_IS_INVALID.getErrorMessage());
        }
    }

    public static void validateMedia(@NonNull final UUID userId,
                                     @NonNull final UUID postId,
                                     @NonNull final MediaDTO mediaDTO,
                                     @NonNull final MultipartFile mediaFile,
                                     @NonNull final String md5,
                                     @NonNull final MediaRepository mediaRepository) throws FilenameIsInvalidException, FileExtensionIsNotMatchingContentException, RecordAlreadyExistsException {

        // Check if filename is valid
        boolean isFilenameValid = MediaExtension.isFilenameValid(
                mediaFile.getOriginalFilename()
        );
        if (!isFilenameValid) {
            throw new FilenameIsInvalidException(Errors.FILENAME_IS_INVALID.getErrorMessage(mediaFile.getOriginalFilename()));
        }


        // Check if file is matching declared extension
        boolean isFileContentValid = MediaExtension.isFileContentValid(
                mediaFile.getOriginalFilename(),
                mediaFile,
                mediaDTO.getExtension()
        );
        if (!isFileContentValid) {
            throw new FileExtensionIsNotMatchingContentException(Errors.FILE_EXTENSION_IS_NOT_MATCHING_CONTENT.getErrorMessage());
        }


        // Check if this post already contains media with md5 of file in this request
        Optional<Media> duplicateMedia = mediaRepository.findByOwnerIdAndPostIdAndMd5(userId, postId, md5);
        if (duplicateMedia.isPresent()) {
            MediaSnapshot duplicateMediaSnapshot = duplicateMedia.get().getSnapshot();

            throw new RecordAlreadyExistsException(Errors.FILE_HASH_DUPLICATE_IN_POST.getErrorMessage(
                    md5,
                    duplicateMediaSnapshot.getPostId()
            ));
        }
    }

}
