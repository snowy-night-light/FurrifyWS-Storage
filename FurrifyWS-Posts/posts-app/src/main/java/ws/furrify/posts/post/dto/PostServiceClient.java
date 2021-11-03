package ws.furrify.posts.post.dto;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ws.furrify.posts.post.dto.query.PostDetailsDTO;

import java.util.UUID;

/**
 * Communication interface with post service.
 *
 * @author Skyte
 */
public interface PostServiceClient {
    /**
     * Get user post.
     *
     * @param userId Owner UUID.
     * @param postId Post UUID.
     * @return Post details from other microservice.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/users/{userId}/posts/{postId}")
    PostDetailsDTO getUserPost(@PathVariable UUID userId, @PathVariable UUID postId);
}
