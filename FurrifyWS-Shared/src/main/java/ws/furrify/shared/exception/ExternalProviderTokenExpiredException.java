package ws.furrify.shared.exception;

import lombok.Getter;

/**
 * @author Skyte
 */
public class ExternalProviderTokenExpiredException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public ExternalProviderTokenExpiredException(String message) {
        super(message);
    }

}