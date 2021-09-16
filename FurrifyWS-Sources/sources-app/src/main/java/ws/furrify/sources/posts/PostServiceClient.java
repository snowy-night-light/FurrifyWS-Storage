package ws.furrify.sources.posts;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ws.furrify.sources.posts.dto.query.AttachmentDetailsQueryDTO;
import ws.furrify.sources.posts.dto.query.MediaDetailsQueryDTO;

import java.util.UUID;

/**
 * Communication interface with post service.
 *
 * @author Skyte
 */
public interface PostServiceClient {
    /**
     * Get post media.
     *
     * @param userId  Owner UUID.
     * @param postId  Post UUID.
     * @param mediaId Media UUID.
     * @return Post media details from other microservice.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/users/{userId}/posts/{postId}/media/{mediaId}")
    MediaDetailsQueryDTO getPostMedia(@PathVariable UUID userId, @PathVariable UUID postId, @PathVariable UUID mediaId);

    /**
     * Get post attachment.
     *
     * @param userId       Owner UUID.
     * @param postId       Post UUID.
     * @param attachmentId Attachment UUID.
     * @return Post attachment details from other microservice.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/users/{userId}/posts/{postId}/attachments/{attachmentId}")
    AttachmentDetailsQueryDTO getPostAttachment(@PathVariable UUID userId, @PathVariable UUID postId, @PathVariable UUID attachmentId);
}
