package ws.furrify.shared.exception;

import lombok.Getter;

/**
 * @author Skyte
 */
public class InvalidDataGivenException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public InvalidDataGivenException(String message) {
        super(message);
    }

}