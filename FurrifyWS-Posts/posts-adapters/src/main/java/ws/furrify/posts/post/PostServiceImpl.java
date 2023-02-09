package ws.furrify.posts.post;

import feign.FeignException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ws.furrify.posts.post.dto.PostServiceClient;
import ws.furrify.posts.post.dto.query.PostDetailsDTO;
import ws.furrify.shared.exception.ChainOfRequestsBrokenException;
import ws.furrify.shared.exception.ChainOfRequestsUnauthorizedException;
import ws.furrify.shared.exception.Errors;

import java.util.UUID;

/**
 * Currently public class as both media and attachment are in the same module.
 *
 * @author sky
 */
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostServiceClient {

    private final PostServiceClientImpl postServiceClient;

    private final static String NAME = "posts";

    @Bulkhead(name = "getUserPost", fallbackMethod = "getUserPostFallback")
    @Override
    public PostDetailsDTO getUserPost(final UUID userId, final UUID postId) {
        return postServiceClient.getUserPost(userId, postId);
    }

    private PostDetailsDTO getUserPostFallback(Throwable throwable) {
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
     * Implements Post Service Client as a Feign Client.
     */
    @FeignClient(value = "POSTS-SERVICE")
    private interface PostServiceClientImpl extends PostServiceClient {
    }
}
