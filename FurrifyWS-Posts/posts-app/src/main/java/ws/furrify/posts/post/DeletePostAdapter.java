package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import ws.furrify.posts.post.vo.PostData;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.time.Instant;
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
                createPostEvent(postId)
        );
    }

    private PostEvent createPostEvent(final UUID postId) {
        return PostEvent.newBuilder()
                .setState(DomainEventPublisher.PostEventType.REMOVED.name())
                .setPostId(postId.toString())
                .setDataBuilder(PostData.newBuilder())
                .setOccurredOn(Instant.now().toEpochMilli())
                .build();
    }
}
