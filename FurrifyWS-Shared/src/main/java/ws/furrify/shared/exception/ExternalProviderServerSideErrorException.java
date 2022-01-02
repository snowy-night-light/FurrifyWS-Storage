package ws.furrify.shared.exception;

import lombok.Getter;

/**
 * @author Skyte
 */
public class ExternalProviderServerSideErrorException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public ExternalProviderServerSideErrorException(String message) {
        super(message);
    }

}