package ws.furrify.posts.post.dto.command;

import ws.furrify.posts.post.dto.PostDTO;

import java.io.Serializable;

/**
 * Interface for post dto commands.
 */
interface PostCommandDTO extends Serializable {

    /**
     * Method enforcing implementation of mapper to PostDTO.
     *
     * @return CommandDTO mapped to PostDTO.
     */
    PostDTO toDTO();

}
