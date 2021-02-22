package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.UUID;

@RequiredArgsConstructor
class DeletePostAdapter implements DeletePostPort {

    private final DomainEventPublisher<PostEvent> domainEventPublisher;
    private final PostRepository postRepository;

    @Override
    public void deletePost(final UUID userId, final UUID postId) {
        if (!postRepository.existsByOwnerIdAndPostId(userId, postId)) {
            throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(postId.toString()));
        }

        // Publish delete post event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.POST,
                // Use userId as key
                userId,
                PostUtils.deletePostEvent(postId)
        );
    }
}
