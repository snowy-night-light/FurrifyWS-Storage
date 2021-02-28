package ws.furrify.posts.post;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ws.furrify.posts.artist.ArtistServiceClient;
import ws.furrify.posts.post.dto.PostDTO;
import ws.furrify.posts.post.vo.PostArtist;
import ws.furrify.posts.post.vo.PostTag;
import ws.furrify.posts.tag.TagServiceClient;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
class CreatePostAdapter implements CreatePostPort {

    private final PostFactory postFactory;
    private final DomainEventPublisher<PostEvent> domainEventPublisher;
    private final TagServiceClient tagServiceClient;
    private final ArtistServiceClient artistServiceClient;

    @Override
    public UUID createPost(@NonNull final UUID userId,
                           @NonNull final PostDTO postDTO) {
        // Generate post uuid
        UUID postId = UUID.randomUUID();

        // Convert tags with values to tags with values and types
        Set<PostTag> tags = PostUtils.tagValueToTagVO(userId, postDTO.getTags(), tagServiceClient);

        // Convert artists with artistId to artists with artistIds and preferredNicknames
        Set<PostArtist> artists = PostUtils.artistWithArtistIdToArtistVO(userId, postDTO.getArtists(), artistServiceClient);

        // Edit postDTO with generated user uuid, encrypted password and current time
        PostDTO updatedPostToCreateDTO = postDTO.toBuilder()
                .postId(postId)
                .ownerId(userId)
                .tags(tags)
                .artists(artists)
                .createDate(ZonedDateTime.now())
                .build();


        // Publish create post event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.POST,
                // User userId as key
                userId,
                PostUtils.createPostEvent(
                        DomainEventPublisher.PostEventType.CREATED,
                        postFactory.from(updatedPostToCreateDTO)
                )
        );

        return postId;
    }
}
