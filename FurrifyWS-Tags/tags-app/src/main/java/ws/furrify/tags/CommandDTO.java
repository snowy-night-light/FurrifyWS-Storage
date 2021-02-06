package ws.furrify.tags;

/**
 * Interface for dto commands.
 *
 * @param <T> A main entity DTO.
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
