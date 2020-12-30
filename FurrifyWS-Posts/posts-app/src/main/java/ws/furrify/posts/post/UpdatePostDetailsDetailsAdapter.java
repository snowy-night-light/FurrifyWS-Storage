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
class UpdatePostDetailsDetailsAdapter implements UpdatePostDetailsPort {

    private final DomainEventPublisher<PostEvent> domainEventPublisher;
    private final PostRepository postRepository;

    @Override
    public void updatePostDetails(final UUID userId, final UUID postId, final PostDTO postDTO) {
        Post post = postRepository.findByOwnerIdAndPostId(userId, postId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(postId.toString())));

        // Update changed fields in post
        if (postDTO.getTitle() != null) {
            post.updateDetails(postDTO.getTitle(), post.getSnapshot().getDescription());
        }
        if (postDTO.getDescription() != null) {
            post.updateDetails(post.getSnapshot().getTitle(), postDTO.getDescription());
        }

        // Publish update user event
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
                .setState(PostEventType.UPDATED.name())
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
