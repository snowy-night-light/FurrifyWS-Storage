package ws.furrify.shared.exception;

/**
 * Custom REST exception template interface.
 *
 * @author Skyte
 */
public interface RestException {

    /**
     * Get http status exception represents.
     *
     * @return HttpStatus enum.
     */
    HttpStatus getStatus();

    /**
     * Get exception message. It can be one of the pre-created ones in Errors enum or your own.
     *
     * @return String value of message.
     */
    String getMessage();
}
