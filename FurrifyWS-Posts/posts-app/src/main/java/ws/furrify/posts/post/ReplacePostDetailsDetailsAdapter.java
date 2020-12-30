package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import ws.furrify.posts.DomainEventPublisher;
import ws.furrify.posts.PostData;
import ws.furrify.posts.PostEvent;
import ws.furrify.posts.exception.Errors;
import ws.furrify.posts.exception.RecordNotFoundException;
import ws.furrify.posts.post.dto.PostDTO;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
class ReplacePostDetailsDetailsAdapter implements ReplacePostDetailsPort {

    private final DomainEventPublisher<PostEvent> domainEventPublisher;
    private final PostRepository postRepository;

    @Override
    public void replacePostDetails(final UUID userId, final UUID postId, final PostDTO postDTO) {
        Post post = postRepository.findByOwnerIdAndPostId(userId, postId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(postId.toString())));

        // Update all details in post
        post.updateDetails(postDTO.getTitle(), postDTO.getDescription());

        // Publish replace post details event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.POST,
                // User userId as key
                userId,
                createPostEvent(post)
        );
    }

    private PostEvent createPostEvent(final Post post) {
        PostSnapshot postSnapshot = post.getSnapshot();

        return PostEvent.newBuilder()
                .setState(PostEventType.REPLACED.name())
                .setPostId(postSnapshot.getId())
                .setPostUUID(postSnapshot.getPostId().toString())
                .setOccurredOn(Instant.now().toEpochMilli())
                .setDataBuilder(
                        PostData.newBuilder()
                                .setOwnerId(postSnapshot.getOwnerId().toString())
                                .setTitle(postSnapshot.getTitle())
                                .setDescription(postSnapshot.getDescription())
                                .setCreateDate(postSnapshot.getCreateDate().toInstant().toEpochMilli())
                ).build();
    }
}
