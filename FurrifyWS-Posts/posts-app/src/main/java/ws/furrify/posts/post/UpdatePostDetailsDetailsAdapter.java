package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import ws.furrify.posts.DomainEventPublisher;
import ws.furrify.posts.PostData;
import ws.furrify.posts.PostEvent;
import ws.furrify.posts.exception.Errors;
import ws.furrify.posts.exception.RecordNotFoundException;
import ws.furrify.posts.post.dto.PostDTO;
import ws.furrify.posts.post.vo.PostTag;
import ws.furrify.posts.tag.TagQueryRepository;
import ws.furrify.posts.vo.PostTagData;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class UpdatePostDetailsDetailsAdapter implements UpdatePostDetailsPort {

    private final DomainEventPublisher<PostEvent> domainEventPublisher;
    private final PostRepository postRepository;
    private final TagQueryRepository tagQueryRepository;

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
        if (postDTO.getTags() != null) {
            // Convert tags with values to tags with values and types
            Set<PostTag> tags = PostTagUtils.tagValueToTag(userId, postDTO.getTags(), tagQueryRepository);

            post.replaceTags(tags);
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
                .setState(DomainEventPublisher.PostEventType.UPDATED.name())
                .setPostId(postSnapshot.getId())
                .setPostUUID(postSnapshot.getPostId().toString())
                .setOccurredOn(Instant.now().toEpochMilli())
                .setDataBuilder(
                        PostData.newBuilder()
                                .setOwnerId(postSnapshot.getOwnerId().toString())
                                .setTitle(postSnapshot.getTitle())
                                .setDescription(postSnapshot.getDescription())
                                .setTags(
                                        // Map PostTag to PostTagData
                                        postSnapshot.getTags().stream()
                                                .map(tag ->
                                                        PostTagData.newBuilder()
                                                                .setValue(tag.getValue())
                                                                .setType(tag.getType())
                                                                .build()
                                                ).collect(Collectors.toList())
                                )
                                .setCreateDate(postSnapshot.getCreateDate().toInstant().toEpochMilli())
                ).build();
    }
}
