package ws.furrify.posts;

/**
 * Interface for dto commands.
 *
 * @author Skyte
 */
public interface CommandDTO<T> {

    /**
     * Method enforcing implementation of mapper to DTO.
     *
     * @return CommandDTO mapped to DTO.
     */
    T toDTO();
}
