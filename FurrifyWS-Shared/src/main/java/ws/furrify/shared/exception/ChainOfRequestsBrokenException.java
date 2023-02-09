package ws.furrify.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author Skyte
 */
public class ChainOfRequestsBrokenException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public ChainOfRequestsBrokenException(String message) {
        super(message);
    }

}