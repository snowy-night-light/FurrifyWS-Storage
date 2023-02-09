package ws.furrify.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

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