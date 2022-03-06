package ws.furrify.posts.attachment.strategy;

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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Upload attachment file to local storage strategy.
 * Needs to be created as bean for @Value to work.
 *
 * @author sky
 */
@RequiredArgsConstructor
public class LocalStorageAttachmentUploadStrategy implements AttachmentUploadStrategy {

    @Value("${LOCAL_STORAGE_ATTACHMENT_PATH:/data/attachment}")
    private String LOCAL_STORAGE_ATTACHMENT_PATH;

    @Value("${REMOTE_STORAGE_ATTACHMENT_PATH:/attachment}")
    private String REMOTE_STORAGE_ATTACHMENT_PATH;

    @Override
    public UploadedAttachmentFile uploadAttachment(final UUID attachmentId, final MultipartFile fileSource) {
        try (
                InputStream attachmentInputStream = fileSource.getInputStream()
        ) {

            // Check if filename is not null
            if (fileSource.getOriginalFilename() == null) {
                throw new IllegalStateException("Filename cannot be empty.");
            }

            // Sanitize filename
            String filename = fileSource.getOriginalFilename().replaceAll("\\s+","_");

            // Create file
            File attachmentFile = new File(LOCAL_STORAGE_ATTACHMENT_PATH + "/" + attachmentId + "/" + filename);

            // Create directories where files need to be located
            boolean wasAttachmentFileCreated = attachmentFile.getParentFile().mkdirs() || attachmentFile.getParentFile().exists();

            if (!wasAttachmentFileCreated) {
                throw new FileUploadCannotCreatePathException(Errors.FILE_UPLOAD_CANNOT_CREATE_PATH.getErrorMessage());
            }

            // Upload file
            writeToFile(attachmentFile, attachmentInputStream);

            // Return created urls
            return new UploadedAttachmentFile(
                    new URI(REMOTE_STORAGE_ATTACHMENT_PATH + "/" + attachmentId + "/" + filename)
            );

        } catch (IOException | URISyntaxException e) {
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
