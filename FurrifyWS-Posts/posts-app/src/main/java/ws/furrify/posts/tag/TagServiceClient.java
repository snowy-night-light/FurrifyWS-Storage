package ws.furrify.posts.tag;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ws.furrify.posts.tag.dto.query.TagDetailsQueryDTO;

import java.util.UUID;

/**
 * Communication interface with tag service.
 *
 * @author Skyte
 */
public interface TagServiceClient {
    /**
     * Get user tag.
     *
     * @param userId Owner UUID.
     * @param value  Tag value.
     * @return Tag details from other microservice.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/users/{userId}/tags/{value}")
    TagDetailsQueryDTO getUserTag(@PathVariable UUID userId, @PathVariable String value);
}
