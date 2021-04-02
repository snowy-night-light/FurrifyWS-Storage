package ws.furrify.posts.post;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ws.furrify.posts.artist.ArtistServiceClient;
import ws.furrify.posts.post.dto.PostDTO;
import ws.furrify.posts.post.vo.PostArtist;
import ws.furrify.posts.post.vo.PostDescription;
import ws.furrify.posts.post.vo.PostTag;
import ws.furrify.posts.post.vo.PostTitle;
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
    public void updatePost(@NonNull final UUID userId,
                           @NonNull final UUID postId,
                           @NonNull final PostDTO postDTO) {
        Post post = postRepository.findByOwnerIdAndPostId(userId, postId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(postId.toString())));

        // Update changed fields in post
        if (postDTO.getTitle() != null) {
            post.updateDetails(
                    PostTitle.of(postDTO.getTitle()),
                    PostDescription.of(post.getSnapshot().getDescription())
            );
        }
        if (postDTO.getDescription() != null) {
            post.updateDetails(
                    PostTitle.of(post.getSnapshot().getTitle()),
                    PostDescription.of(postDTO.getDescription())
            );
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

        // Publish update post event
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
