package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import ws.furrify.posts.artist.ArtistServiceClient;
import ws.furrify.posts.post.dto.PostDTO;
import ws.furrify.posts.post.vo.PostArtist;
import ws.furrify.posts.post.vo.PostTag;
import ws.furrify.posts.tag.TagServiceClient;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
class UpdatePostAdapter implements UpdatePostPort {

    private final DomainEventPublisher<PostEvent> domainEventPublisher;
    private final PostRepository postRepository;
    private final TagServiceClient tagServiceClient;
    private final ArtistServiceClient artistServiceClient;

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
            Set<PostTag> tags = PostUtils.tagValueToTagVO(userId, postDTO.getTags(), tagServiceClient);

            post.replaceTags(tags);
        }
        if (postDTO.getArtists() != null) {
            // Convert artists with artistId to artists with artistIds and preferredNicknames
            Set<PostArtist> artists = PostUtils.artistWithArtistIdToArtistVO(userId, postDTO.getArtists(), artistServiceClient);

            post.replaceArtists(artists);
        }

        // Publish update user event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.POST,
                // User userId as key
                userId,
                PostUtils.createPostEvent(
                        DomainEventPublisher.PostEventType.UPDATED,
                        post
                )
        );
    }
}
