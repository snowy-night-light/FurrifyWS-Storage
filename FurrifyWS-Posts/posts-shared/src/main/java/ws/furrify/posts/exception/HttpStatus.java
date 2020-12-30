package ws.furrify.posts.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
enum HttpStatus {
    /**
     * Http status codes
     */

    NOT_FOUND(404),
    CONFLICT(409),
    BAD_REQUEST(400),
    INTERNAL_SERVER_ERROR(500);

    private final int status;

}
