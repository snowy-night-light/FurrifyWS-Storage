package ws.furrify.shared.exception;

import lombok.Getter;

/**
 * @author Skyte
 */
public class ChainOfRequestsUnauthorizedException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.UNAUTHORIZED;

    public ChainOfRequestsUnauthorizedException(String message) {
        super(message);
    }

}