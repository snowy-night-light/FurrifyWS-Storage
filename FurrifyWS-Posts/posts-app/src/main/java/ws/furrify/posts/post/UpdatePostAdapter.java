package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import ws.furrify.posts.PostEvent;
import ws.furrify.posts.post.dto.PostDTO;
import ws.furrify.posts.post.vo.PostTag;
import ws.furrify.posts.tag.TagServiceClient;
import ws.furrify.posts.vo.PostData;
import ws.furrify.posts.vo.PostTagData;
import ws.furrify.shared.DomainEventPublisher;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class UpdatePostAdapter implements UpdatePostPort {

    private final DomainEventPublisher<PostEvent> domainEventPublisher;
    private final PostRepository postRepository;
    private final TagServiceClient tagServiceClient;

    @Override
    public void updatePost(final UUID userId, final UUID postId, final PostDTO postDTO) {
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
            Set<PostTag> tags = PostTagUtils.tagValueToTagVO(userId, postDTO.getTags(), tagServiceClient);

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
