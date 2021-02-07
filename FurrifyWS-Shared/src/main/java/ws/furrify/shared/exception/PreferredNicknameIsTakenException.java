package ws.furrify.shared.exception;

import lombok.Getter;

/**
 * @author Skyte
 */
public class PreferredNicknameIsTakenException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.CONFLICT;

    public PreferredNicknameIsTakenException(String message) {
        super(message);
    }

}