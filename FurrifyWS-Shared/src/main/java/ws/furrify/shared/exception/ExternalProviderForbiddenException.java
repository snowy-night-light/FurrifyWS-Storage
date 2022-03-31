package ws.furrify.shared.exception;

import lombok.Getter;

/**
 * @author Skyte
 */
public class ExternalProviderForbiddenException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public ExternalProviderForbiddenException(String message) {
        super(message);
    }

}