package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import ws.furrify.posts.DomainEventPublisher;
import ws.furrify.posts.PostData;
import ws.furrify.posts.PostEvent;
import ws.furrify.posts.post.dto.PostDTO;
import ws.furrify.posts.post.vo.PostTag;
import ws.furrify.posts.tag.TagQueryRepository;
import ws.furrify.posts.vo.PostTagData;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class CreatePostAdapter implements CreatePostPort {

    private final PostFactory postFactory;
    private final DomainEventPublisher<PostEvent> domainEventPublisher;
    private final TagQueryRepository tagQueryRepository;

    @Override
    public UUID createPost(final UUID userId, final PostDTO postDTO) {
        // Generate post uuid
        UUID postId = UUID.randomUUID();

        // Convert tags with values to tags with values and types
        Set<PostTag> tags = PostTagUtils.tagValueToTag(userId, postDTO.getTags(), tagQueryRepository);

        // Edit postDTO with generated user uuid, encrypted password and current time
        PostDTO updatedPostToCreateDTO = postDTO.toBuilder()
                .postId(postId)
                .ownerId(userId)
                .tags(tags)
                .createDate(ZonedDateTime.now())
                .build();


        // Publish create post event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.POST,
                // User userId as key
                userId,
                createPostEvent(postFactory.from(updatedPostToCreateDTO))
        );

        return postId;
    }

    private PostEvent createPostEvent(final Post post) {
        PostSnapshot postSnapshot = post.getSnapshot();

        return PostEvent.newBuilder()
                .setState(DomainEventPublisher.PostEventType.CREATED.name())
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
                                ).setCreateDate(postSnapshot.getCreateDate().toInstant().toEpochMilli())
                ).build();
    }
}
