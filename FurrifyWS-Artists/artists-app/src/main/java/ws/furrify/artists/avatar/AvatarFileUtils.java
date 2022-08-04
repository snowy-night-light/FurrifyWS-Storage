package ws.furrify.artists.avatar;

import lombok.NonNull;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.FileContentIsCorruptedException;
import ws.furrify.shared.exception.FileExtensionIsNotMatchingContentException;
import ws.furrify.shared.exception.FilenameIsInvalidException;
import ws.furrify.shared.exception.RecordAlreadyExistsException;

import java.io.IOException;

/**
 * Utils class regarding Avatar files.
 *
 * @author Skyte
 */
class AvatarFileUtils {

    public static String generateMd5FromFile(@NonNull final MultipartFile mediaFile) {
        try {
            // Get file hash
            return DigestUtils.md5Hex(mediaFile.getInputStream());
        } catch (IOException e) {
            throw new FileContentIsCorruptedException(Errors.FILE_CONTENT_IS_CORRUPTED.getErrorMessage());
        }
    }

    public static void validateAvatar(@NonNull final AvatarExtension extension,
                                      @NonNull final MultipartFile avatarFile) throws FilenameIsInvalidException, FileExtensionIsNotMatchingContentException, RecordAlreadyExistsException {

        // Check if file is matching declared extension
        boolean isFileContentValid = AvatarExtension.isFileContentValid(
                avatarFile.getOriginalFilename(),
                avatarFile,
                extension
        );
        if (!isFileContentValid) {
            throw new FileExtensionIsNotMatchingContentException(Errors.FILE_EXTENSION_IS_NOT_MATCHING_CONTENT.getErrorMessage());
        }

        // Check if filename is valid
        boolean isFilenameValid = AvatarExtension.isFilenameValid(
                avatarFile.getOriginalFilename()
        );
        if (!isFilenameValid) {
            throw new FilenameIsInvalidException(Errors.FILENAME_IS_INVALID.getErrorMessage(avatarFile.getOriginalFilename()));
        }
    }

}
