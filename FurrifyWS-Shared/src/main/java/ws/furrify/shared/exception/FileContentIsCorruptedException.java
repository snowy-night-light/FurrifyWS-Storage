package ws.furrify.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author Skyte
 */
public class FileContentIsCorruptedException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public FileContentIsCorruptedException(String message) {
        super(message);
    }

}