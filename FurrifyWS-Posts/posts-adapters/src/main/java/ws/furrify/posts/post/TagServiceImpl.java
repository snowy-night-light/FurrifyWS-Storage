package ws.furrify.posts.post;

import feign.FeignException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ws.furrify.posts.tag.TagServiceClient;
import ws.furrify.posts.tag.dto.query.TagDetailsQueryDTO;
import ws.furrify.shared.exception.ChainOfRequestsBrokenException;
import ws.furrify.shared.exception.ChainOfRequestsUnauthorizedException;
import ws.furrify.shared.exception.Errors;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class TagServiceImpl implements TagServiceClient {

    private final TagServiceClientImpl tagServiceClient;

    private final static String NAME = "tags";

    @Bulkhead(name = "getUserTag", fallbackMethod = "getUserTagFallback")
    @Override
    public TagDetailsQueryDTO getUserTag(final UUID userId, final String value) {
        return tagServiceClient.getUserTag(userId, value);
    }

    private TagDetailsQueryDTO getUserTagFallback(Throwable throwable) {
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
     * Implements Tag Service Client as a Feign Client.
     */
    @FeignClient(name = "TAGS-SERVICE", url = "TAGS-SERVICE")
    private interface TagServiceClientImpl extends TagServiceClient {
    }
}
