package ws.furrify.shared.exception;

import lombok.Getter;

/**
 * @author Skyte
 */
public class HardLimitForEntityTypeException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public HardLimitForEntityTypeException(String message) {
        super(message);
    }

}