package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import ws.furrify.posts.PostEvent;
import ws.furrify.posts.post.dto.PostDTO;
import ws.furrify.posts.post.vo.PostTag;
import ws.furrify.posts.tag.TagServiceClient;
import ws.furrify.posts.vo.PostData;
import ws.furrify.posts.vo.PostTagData;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class ReplacePostAdapter implements ReplacePostPort {

    private final DomainEventPublisher<PostEvent> domainEventPublisher;
    private final PostRepository postRepository;
    private final TagServiceClient tagServiceClient;

    @Override
    public void replacePost(final UUID userId, final UUID postId, final PostDTO postDTO) {
        Post post = postRepository.findByOwnerIdAndPostId(userId, postId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(postId.toString())));

        // Convert tags with values to tags with values and types
        Set<PostTag> tags = PostTagUtils.tagValueToTagVO(userId, postDTO.getTags(), tagServiceClient);

        // Update tags in post
        post.replaceTags(tags);

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
                .setState(DomainEventPublisher.PostEventType.REPLACED.name())
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
