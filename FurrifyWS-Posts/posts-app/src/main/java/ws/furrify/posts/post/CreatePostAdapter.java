package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import ws.furrify.posts.DomainEventPublisher;
import ws.furrify.posts.PostData;
import ws.furrify.posts.PostEvent;
import ws.furrify.posts.post.dto.PostDTO;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

@RequiredArgsConstructor
class CreatePostAdapter implements CreatePostPort {

    private final PostFactory postFactory;
    private final DomainEventPublisher<PostEvent> domainEventPublisher;

    @Override
    public UUID createPost(final UUID userId, final PostDTO postDTO) {
        // Generate post uuid
        UUID postId = UUID.randomUUID();

        // Edit postDTO with generated user uuid, encrypted password and current time
        PostDTO updatedPostToCreateDTO = postDTO.toBuilder()
                .postId(postId)
                .ownerId(userId)
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
                                .setCreateDate(postSnapshot.getCreateDate().toInstant().toEpochMilli())
                ).build();
    }
}
