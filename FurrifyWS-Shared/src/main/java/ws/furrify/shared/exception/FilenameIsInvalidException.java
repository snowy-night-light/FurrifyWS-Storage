package ws.furrify.shared.exception;

import lombok.Getter;

/**
 * @author Skyte
 */
public class FilenameIsInvalidException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public FilenameIsInvalidException(String message) {
        super(message);
    }

}