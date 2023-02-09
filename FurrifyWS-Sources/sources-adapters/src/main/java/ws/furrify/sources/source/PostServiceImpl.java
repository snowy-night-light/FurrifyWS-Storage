package ws.furrify.sources.source;

import feign.FeignException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ws.furrify.shared.exception.ChainOfRequestsBrokenException;
import ws.furrify.shared.exception.ChainOfRequestsUnauthorizedException;
import ws.furrify.shared.exception.Errors;
import ws.furrify.sources.posts.PostServiceClient;
import ws.furrify.sources.posts.dto.query.AttachmentDetailsQueryDTO;
import ws.furrify.sources.posts.dto.query.MediaDetailsQueryDTO;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class PostServiceImpl implements PostServiceClient {

    private final PostServiceClientImpl postServiceClient;

    private final static String NAME = "posts";

    @Bulkhead(name = "getPostAttachment", fallbackMethod = "getPostAttachmentFallback")
    @Override
    public AttachmentDetailsQueryDTO getPostAttachment(final UUID userId, final UUID postId, final UUID attachmentId) {
        return postServiceClient.getPostAttachment(userId, postId, attachmentId);
    }

    @Bulkhead(name = "getPostMedia", fallbackMethod = "getPostMediaFallback")
    @Override
    public MediaDetailsQueryDTO getPostMedia(final UUID userId, final UUID postId, final UUID mediaId) {
        return postServiceClient.getPostMedia(userId, postId, mediaId);
    }

    private MediaDetailsQueryDTO getPostMediaFallback(Throwable throwable) {
        return fallback(throwable);
    }

    private AttachmentDetailsQueryDTO getPostAttachmentFallback(Throwable throwable) {
        return fallback(throwable);
    }

    private <T> T fallback(Throwable throwable) {
        var exception = (FeignException) throwable;

        HttpStatus status = HttpStatus.valueOf(exception.status());

        switch (status) {
            case NOT_FOUND -> {
                return null;
            }

            case FORBIDDEN -> throw new ChainOfRequestsUnauthorizedException(Errors.CHAIN_OF_REQUESTS_UNAUTHORIZED.getErrorMessage(NAME));

            default -> throw new ChainOfRequestsBrokenException(Errors.CHAIN_OF_REQUESTS_BROKEN.getErrorMessage(NAME));
        }
    }

    /**
     * Implements Attachment Service Client as a Feign Client.
     */
    @FeignClient(name = "POSTS-SERVICE", url = "POSTS-SERVICE")
    private interface PostServiceClientImpl extends PostServiceClient {
    }
}
