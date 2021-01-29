package ws.furrify.shared.exception;

import lombok.Getter;

/**
 * @author Skyte
 */
public class RecordNotFoundException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.NOT_FOUND;

    public RecordNotFoundException(String message) {
        super(message);
    }

}