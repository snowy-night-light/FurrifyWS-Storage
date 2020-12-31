package ws.furrify.posts;

import java.io.Serializable;

/**
 * Interface for dto commands.
 *
 * @author Skyte
 */
public interface CommandDTO<T> extends Serializable {

    /**
     * Method enforcing implementation of mapper to DTO.
     *
     * @return CommandDTO mapped to DTO.
     */
    T toDTO();
}
