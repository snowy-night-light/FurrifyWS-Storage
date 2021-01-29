package ws.furrify.shared.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Http status codes in enum.
 *
 * @author Skyte
 */
@Getter
@RequiredArgsConstructor
public enum HttpStatus {
    /**
     * Http status codes
     */

    NOT_FOUND(404),
    CONFLICT(409),
    BAD_REQUEST(400),
    INTERNAL_SERVER_ERROR(500);

    private final int status;

}
