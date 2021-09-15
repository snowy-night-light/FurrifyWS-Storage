package ws.furrify.shared.exception;

import lombok.Getter;

/**
 * @author Skyte
 */
public class StrategyNotFoundException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.NOT_FOUND;

    public StrategyNotFoundException(String message) {
        super(message);
    }

}