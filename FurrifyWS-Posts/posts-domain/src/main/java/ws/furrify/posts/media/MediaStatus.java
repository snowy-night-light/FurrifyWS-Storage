package ws.furrify.posts.media;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Media status on processing server.
 *
 * @author Skyte
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum MediaStatus {
    /**
     * Request was sent to processing server.
     */
    REQUEST_PENDING,
    /**
     * Processing server accepted request and will start processing soon.
     */
    REQUEST_ACCEPTED,
    /**
     * File is being uploaded to its destination.
     */
    UPLOAD_PROCESS,
    /**
     * Upload to designation has failed and will require manual retry.
     */
    UPLOAD_FAIL,
    /**
     * File is being classified by neural networks.
     */
    CLASSIFICATION_IN_PROGRESS,
    /**
     * Neural networks has failed to classify the file and will manual retry or skip.
     */
    CLASSIFICATION_FAIL,
    /**
     * File has passed all checks and was accepted.
     */
    ACCEPTED,
    /**
     * File was marked was probable duplicate and will require manual review by user.
     */
    DUPLICATE
}
