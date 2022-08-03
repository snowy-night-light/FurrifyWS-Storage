package ws.furrify.posts.attachment;

import lombok.NonNull;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.attachment.dto.AttachmentDTO;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.FileContentIsCorruptedException;
import ws.furrify.shared.exception.FileExtensionIsNotMatchingContentException;
import ws.furrify.shared.exception.FilenameIsInvalidException;
import ws.furrify.shared.exception.RecordAlreadyExistsException;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * Utils class regarding Attachment files.
 *
 * @author Skyte
 */
class AttachmentFileUtils {

    public static String generateMd5FromFile(@NonNull final MultipartFile mediaFile) {
        try {
            // Get file hash
            return DigestUtils.md5Hex(mediaFile.getInputStream());
        } catch (IOException e) {
            throw new FileContentIsCorruptedException(Errors.FILE_CONTENT_IS_CORRUPTED.getErrorMessage());
        }
    }

    public static void validateAttachment(@NonNull final UUID userId,
                                          @NonNull final UUID postId,
                                          @NonNull final AttachmentDTO attachmentDTO,
                                          @NonNull final MultipartFile attachmentFile,
                                          @NonNull final String md5,
                                          @NonNull final AttachmentRepository attachmentRepository) throws FilenameIsInvalidException, FileExtensionIsNotMatchingContentException, RecordAlreadyExistsException {

        // Check if file is matching declared extension
        boolean isFileContentValid = AttachmentExtension.isFileContentValid(
                attachmentFile.getOriginalFilename(),
                attachmentFile,
                attachmentDTO.getExtension()
        );
        if (!isFileContentValid) {
            throw new FileExtensionIsNotMatchingContentException(Errors.FILE_EXTENSION_IS_NOT_MATCHING_CONTENT.getErrorMessage());
        }

        // Check if filename is valid
        boolean isFilenameValid = AttachmentExtension.isFilenameValid(
                attachmentFile.getOriginalFilename()
        );
        if (!isFilenameValid) {
            throw new FilenameIsInvalidException(Errors.FILENAME_IS_INVALID.getErrorMessage(attachmentFile.getOriginalFilename()));
        }

        // Check if this post already contains attachment with md5 of file in this request
        Optional<Attachment> duplicateAttachment = attachmentRepository.findByOwnerIdAndPostIdAndMd5(userId, postId, md5);
        if (duplicateAttachment.isPresent()) {
            AttachmentSnapshot duplicateAttachmentSnapshot = duplicateAttachment.get().getSnapshot();

            throw new RecordAlreadyExistsException(Errors.FILE_HASH_DUPLICATE_IN_POST.getErrorMessage(
                    md5,
                    duplicateAttachmentSnapshot.getPostId()
            ));
        }
    }

}
