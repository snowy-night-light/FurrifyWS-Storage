package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import ws.furrify.artists.artist.ArtistEvent;
import ws.furrify.posts.media.MediaEvent;
import ws.furrify.posts.post.dto.PostDTO;
import ws.furrify.posts.post.dto.PostDtoFactory;
import ws.furrify.posts.post.vo.PostTag;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.tags.tag.TagEvent;

import java.net.URL;
import java.util.UUID;

/**
 * @author Skyte
 */
@RequiredArgsConstructor
@Log
public class PostFacade {

    private final CreatePostPort createPostAdapter;
    private final DeletePostPort deletePostAdapter;
    private final UpdatePostPort updatePostAdapter;
    private final ReplacePostPort replacePostAdapter;
    private final PostRepository postRepository;
    private final PostFactory postFactory;
    private final PostDtoFactory postDTOFactory;

    /**
     * Handle incoming post events.
     *
     * @param postEvent Post event instance received from kafka.
     */
    void handleEvent(final UUID key, final PostEvent postEvent) {
        PostDTO postDTO = postDTOFactory.from(key, postEvent);

        switch (DomainEventPublisher.PostEventType.valueOf(postEvent.getState())) {
            case CREATED, REPLACED, UPDATED -> savePost(postDTO);
            case REMOVED -> deletePostByPostId(postDTO.getPostId());

            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + postEvent.getState() + " Topic=post_events");
        }
    }

    /**
     * Handle incoming media events.
     *
     * @param mediaEvent Media event instance received from kafka.
     */
    @SneakyThrows
    void handleEvent(final UUID key, final MediaEvent mediaEvent) {
        switch (DomainEventPublisher.MediaEventType.valueOf(mediaEvent.getState())) {
            case REMOVED -> deleteMediaFromPost(
                    key,
                    UUID.fromString(mediaEvent.getData().getPostId()),
                    UUID.fromString(mediaEvent.getMediaId())
            );
            case UPDATED, REPLACED -> updateMediaDetailsInPost(key, UUID.fromString(mediaEvent.getData().getPostId()),
                    UUID.fromString(mediaEvent.getMediaId()),
                    mediaEvent.getData().getPriority(),
                    (mediaEvent.getData().getThumbnailUrl() != null) ? new URL(mediaEvent.getData().getThumbnailUrl()) : null,
                    mediaEvent.getData().getExtension(),
                    mediaEvent.getData().getStatus()
            );
            case CREATED -> {
            }
            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + mediaEvent.getState() + " Topic=media_events");
        }
    }

    /**
     * Handle incoming tag events.
     *
     * @param tagEvent Tag event instance received from kafka.
     */
    void handleEvent(final UUID key, final TagEvent tagEvent) {
        switch (DomainEventPublisher.TagEventType.valueOf(tagEvent.getState())) {
            case REMOVED -> deleteTagFromPosts(key, tagEvent.getTagValue());
            case UPDATED, REPLACED -> updateTagDetailsInPosts(key,
                    tagEvent.getTagValue(),
                    tagEvent.getData().getValue(),
                    tagEvent.getData().getType());

            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + tagEvent.getState() + " Topic=tag_events");
        }
    }

    /**
     * Handle incoming artist events.
     *
     * @param artistEvent Artist event instance received from kafka.
     */
    void handleEvent(final UUID key, final ArtistEvent artistEvent) {
        switch (DomainEventPublisher.ArtistEventType.valueOf(artistEvent.getState())) {
            case REMOVED -> deleteArtistFromPosts(key, UUID.fromString(artistEvent.getArtistId()));
            case UPDATED, REPLACED -> updateArtistDetailsInPosts(key,
                    UUID.fromString(artistEvent.getArtistId()),
                    artistEvent.getData().getPreferredNickname());

            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + artistEvent.getState() + " Topic=artist_events");
        }
    }

    /**
     * Creates post.
     *
     * @param userId  User uuid to assign post to.
     * @param postDTO Post to create.
     * @return Created post UUID.
     */
    public UUID createPost(final UUID userId, final PostDTO postDTO) {
        return createPostAdapter.createPost(userId, postDTO);
    }

    /**
     * Deletes post.
     *
     * @param userId Post owner UUID.
     * @param postId Post UUID.
     */
    public void deletePost(final UUID userId, final UUID postId) {
        deletePostAdapter.deletePost(userId, postId);
    }

    /**
     * Replaces all fields in post.
     *
     * @param userId  Post owner UUID.
     * @param postId  Post UUID
     * @param postDTO Replacement post.
     */
    public void replacePost(final UUID userId, final UUID postId, final PostDTO postDTO) {
        replacePostAdapter.replacePost(userId, postId, postDTO);
    }

    /**
     * Updates specified fields in post.
     *
     * @param userId  Post owner UUID.
     * @param postId  Post UUID.
     * @param postDTO Post with updated specific fields.
     */
    public void updatePost(final UUID userId, final UUID postId, final PostDTO postDTO) {
        updatePostAdapter.updatePost(userId, postId, postDTO);
    }

    private void savePost(final PostDTO postDTO) {
        postRepository.save(postFactory.from(postDTO));
    }

    private void deletePostByPostId(final UUID postId) {
        postRepository.deleteByPostId(postId);
    }

    private void updateTagDetailsInPosts(final UUID ownerId,
                                         final String originalTagValue,
                                         final String newValue,
                                         final String newType) {
        // Get all posts with tag value, update value and save.
        postRepository.findAllByOwnerIdAndValueInTags(ownerId, originalTagValue).stream()
                .peek(post -> post.updateTagDetailsInTags(
                        originalTagValue,
                        new PostTag(newValue, newType)
                ))
                .forEach(postRepository::save);
    }

    private void deleteTagFromPosts(final UUID ownerId,
                                    final String tagValue) {
        // Get all posts with tag value, remove it and save.
        postRepository.findAllByOwnerIdAndValueInTags(ownerId, tagValue).stream()
                .peek(post -> post.removeTag(tagValue))
                .forEach(postRepository::save);
    }

    private void deleteArtistFromPosts(final UUID ownerId,
                                       final UUID artistId) {
        // Get all posts with tag value, remove it and save.
        postRepository.findAllByOwnerIdAndArtistIdInArtists(ownerId, artistId).stream()
                .peek(post -> post.removeArtist(artistId))
                .forEach(postRepository::save);
    }


    private void updateArtistDetailsInPosts(final UUID ownerId,
                                            final UUID artistId,
                                            final String preferredNickname) {
        // Get all posts with artistId in artists, update preferredNickname and save.
        postRepository.findAllByOwnerIdAndArtistIdInArtists(ownerId, artistId).stream()
                .peek(post -> post.updateArtistDetailsInArtists(artistId, preferredNickname))
                .forEach(postRepository::save);
    }

    private void deleteMediaFromPost(final UUID ownerId,
                                     final UUID postId,
                                     final UUID mediaId) {
        Post post = postRepository.findByOwnerIdAndPostIdAndMediaId(ownerId, postId, mediaId)
                .orElseThrow(() -> new IllegalStateException("Received request from kafka contains invalid uuid's."));
        post.removeMedia(mediaId);

        postRepository.save(post);
    }

    private void updateMediaDetailsInPost(final UUID ownerId,
                                          final UUID postId,
                                          final UUID mediaId,
                                          final Integer priority,
                                          final URL thumbnailUrl,
                                          final String extension,
                                          final String status) {
        Post post = postRepository.findByOwnerIdAndPostIdAndMediaId(ownerId, postId, mediaId)
                .orElseThrow(() -> new IllegalStateException("Received request from kafka contains invalid uuid's."));
        post.updateMediaDetailsInMediaSet(
                mediaId,
                priority,
                thumbnailUrl,
                extension,
                status
        );

        postRepository.save(post);
    }
}
