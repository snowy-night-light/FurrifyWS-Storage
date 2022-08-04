package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import ws.furrify.artists.artist.ArtistEvent;
import ws.furrify.posts.attachment.AttachmentEvent;
import ws.furrify.posts.avatar.AvatarEvent;
import ws.furrify.posts.media.MediaEvent;
import ws.furrify.posts.post.dto.PostDTO;
import ws.furrify.posts.post.dto.PostDtoFactory;
import ws.furrify.posts.post.vo.PostAttachment;
import ws.furrify.posts.post.vo.PostMedia;
import ws.furrify.posts.post.vo.PostTag;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.tags.tag.TagEvent;

import java.net.URI;
import java.util.UUID;

/**
 * @author Skyte
 */
@RequiredArgsConstructor
@Log
public class PostFacade {

    private final CreatePost createPostImpl;
    private final DeletePost deletePostImpl;
    private final UpdatePost updatePostImpl;
    private final ReplacePost replacePostImpl;
    private final PostRepository postRepository;
    private final PostFactory postFactory;
    private final PostDtoFactory postDTOFactory;

    /**
     * Handle incoming post events.
     *
     * @param postEvent Post event instance received from kafka.
     */
    public void handleEvent(final UUID key, final PostEvent postEvent) {
        PostDTO postDTO = postDTOFactory.from(key, postEvent);

        switch (DomainEventPublisher.PostEventType.valueOf(postEvent.getState())) {
            case CREATED, REPLACED, UPDATED -> savePostInDatabase(postDTO);
            case REMOVED -> deletePostByPostIdFromDatabase(postDTO.getPostId());

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
    public void handleEvent(final UUID key, final MediaEvent mediaEvent) {
        UUID mediaId = UUID.fromString(mediaEvent.getMediaId());

        switch (DomainEventPublisher.MediaEventType.valueOf(mediaEvent.getState())) {
            case REMOVED -> deleteMediaFromPost(
                    key,
                    UUID.fromString(mediaEvent.getData().getPostId()),
                    mediaId
            );
            case UPDATED, REPLACED -> updateMediaDetailsInPost(
                    key,
                    UUID.fromString(mediaEvent.getData().getPostId()),
                    // Build post media from media event
                    PostMedia.builder()
                            .mediaId(mediaId)
                            .fileUri(
                                    new URI(mediaEvent.getData().getFileUri())
                            )
                            .extension(mediaEvent.getData().getExtension())
                            .thumbnailUri(
                                    new URI(mediaEvent.getData().getThumbnailUri())
                            )
                            .priority(mediaEvent.getData().getPriority())
                            .build()
            );
            case CREATED -> addMediaToPost(
                    key,
                    UUID.fromString(mediaEvent.getData().getPostId()),
                    // Build post media from media event
                    PostMedia.builder()
                            .mediaId(mediaId)
                            .fileUri(
                                    new URI(mediaEvent.getData().getFileUri())
                            )
                            .extension(mediaEvent.getData().getExtension())
                            .thumbnailUri(
                                    (mediaEvent.getData().getThumbnailUri() == null) ?
                                            null :
                                            new URI(mediaEvent.getData().getThumbnailUri())
                            )
                            .priority(mediaEvent.getData().getPriority())
                            .build()
            );
            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + mediaEvent.getState() + " Topic=media_events");
        }
    }

    /**
     * Handle incoming attachment events.
     *
     * @param attachmentEvent Attachment event instance received from kafka.
     */
    @SneakyThrows
    public void handleEvent(final UUID key, final AttachmentEvent attachmentEvent) {
        UUID attachmentId = UUID.fromString(attachmentEvent.getAttachmentId());

        switch (DomainEventPublisher.AttachmentEventType.valueOf(attachmentEvent.getState())) {
            case REMOVED -> deleteAttachmentFromPost(
                    key,
                    UUID.fromString(attachmentEvent.getData().getPostId()),
                    attachmentId
            );
            case UPDATED, REPLACED -> updateAttachmentDetailsInPost(
                    key,
                    UUID.fromString(attachmentEvent.getData().getPostId()),
                    // Build post attachment from attachment event
                    PostAttachment.builder()
                            .attachmentId(attachmentId)
                            .fileUri(
                                    new URI(attachmentEvent.getData().getFileUri())
                            )
                            .extension(attachmentEvent.getData().getExtension())
                            .filename(attachmentEvent.getData().getFilename())
                            .build()
            );
            case CREATED -> addAttachmentToPost(
                    key,
                    UUID.fromString(attachmentEvent.getData().getPostId()),
                    // Build post attachment from attachment event
                    PostAttachment.builder()
                            .attachmentId(attachmentId)
                            .fileUri(
                                    new URI(attachmentEvent.getData().getFileUri())
                            )
                            .extension(attachmentEvent.getData().getExtension())
                            .filename(attachmentEvent.getData().getFilename())
                            .build()
            );
            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + attachmentEvent.getState() + " Topic=attachments_events");
        }
    }

    /**
     * Handle incoming avatar events.
     *
     * @param avatarEvent Avatar event instance received from kafka.
     */
    @SneakyThrows
    public void handleEvent(final UUID key, final AvatarEvent avatarEvent) {
        UUID artistId = UUID.fromString(avatarEvent.getData().getArtistId());

        switch (DomainEventPublisher.AvatarEventType.valueOf(avatarEvent.getState())) {
            case REMOVED -> deleteArtistAvatarFromPost(
                    key,
                    artistId
            );
            case CREATED -> addArtistAvatarToPost(
                    key,
                    artistId,
                    new URI(avatarEvent.getData().getThumbnailUri())
            );
            case REPLACED, UPDATED -> replaceArtistAvatarInPost(
                    key,
                    artistId,
                    new URI(avatarEvent.getData().getThumbnailUri())
            );
            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + avatarEvent.getState() + " Topic=avatar_events");
        }
    }

    /**
     * Handle incoming tag events.
     *
     * @param tagEvent Tag event instance received from kafka.
     */
    public void handleEvent(final UUID key, final TagEvent tagEvent) {
        switch (DomainEventPublisher.TagEventType.valueOf(tagEvent.getState())) {
            case CREATED -> {
            }
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
    public void handleEvent(final UUID key, final ArtistEvent artistEvent) {
        switch (DomainEventPublisher.ArtistEventType.valueOf(artistEvent.getState())) {
            case CREATED -> {
            }
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
        return createPostImpl.createPost(userId, postDTO);
    }

    /**
     * Deletes post.
     *
     * @param userId Post owner UUID.
     * @param postId Post UUID.
     */
    public void deletePost(final UUID userId, final UUID postId) {
        deletePostImpl.deletePost(userId, postId);
    }

    /**
     * Replaces all fields in post.
     *
     * @param userId  Post owner UUID.
     * @param postId  Post UUID
     * @param postDTO Replacement post.
     */
    public void replacePost(final UUID userId, final UUID postId, final PostDTO postDTO) {
        replacePostImpl.replacePost(userId, postId, postDTO);
    }

    /**
     * Updates specified fields in post.
     *
     * @param userId  Post owner UUID.
     * @param postId  Post UUID.
     * @param postDTO Post with updated specific fields.
     */
    public void updatePost(final UUID userId, final UUID postId, final PostDTO postDTO) {
        updatePostImpl.updatePost(userId, postId, postDTO);
    }

    private void savePostInDatabase(final PostDTO postDTO) {
        postRepository.save(postFactory.from(postDTO));
    }

    private void deletePostByPostIdFromDatabase(final UUID postId) {
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

    private void addMediaToPost(final UUID ownerId,
                                final UUID postId,
                                final PostMedia postMedia) {
        Post post = postRepository.findByOwnerIdAndPostId(ownerId, postId)
                .orElseThrow(() -> new IllegalStateException("Received request from kafka contains invalid uuid's."));
        post.addMedia(postMedia);

        postRepository.save(post);
    }

    private void addAttachmentToPost(final UUID ownerId,
                                     final UUID postId,
                                     final PostAttachment postAttachment) {
        Post post = postRepository.findByOwnerIdAndPostId(ownerId, postId)
                .orElseThrow(() -> new IllegalStateException("Received request from kafka contains invalid uuid's."));
        post.addAttachment(postAttachment);

        postRepository.save(post);
    }

    private void deleteMediaFromPost(final UUID ownerId,
                                     final UUID postId,
                                     final UUID mediaId) {
        Post post = postRepository.findByOwnerIdAndPostIdAndMediaId(ownerId, postId, mediaId)
                .orElseThrow(() -> new IllegalStateException("Received request from kafka contains invalid uuid's."));
        post.removeMedia(mediaId);

        postRepository.save(post);
    }

    private void deleteAttachmentFromPost(final UUID ownerId,
                                          final UUID postId,
                                          final UUID attachmentId) {
        Post post = postRepository.findByOwnerIdAndPostIdAndAttachmentId(ownerId, postId, attachmentId)
                .orElseThrow(() -> new IllegalStateException("Received request from kafka contains invalid uuid's."));
        post.removeAttachment(attachmentId);

        postRepository.save(post);
    }

    private void updateMediaDetailsInPost(
            final UUID ownerId,
            final UUID postId,
            final PostMedia postMedia) {
        Post post = postRepository.findByOwnerIdAndPostIdAndMediaId(ownerId, postId, postMedia.getMediaId())
                .orElseThrow(() -> new IllegalStateException("Received request from kafka contains invalid uuid's."));
        post.updateMediaDetailsInMediaSet(postMedia);

        postRepository.save(post);
    }

    private void updateAttachmentDetailsInPost(final UUID ownerId,
                                               final UUID postId,
                                               final PostAttachment postAttachment) {
        Post post = postRepository.findByOwnerIdAndPostIdAndAttachmentId(ownerId, postId, postAttachment.getAttachmentId())
                .orElseThrow(() -> new IllegalStateException("Received request from kafka contains invalid uuid's."));
        post.updateAttachmentDetailsInAttachments(postAttachment);

        postRepository.save(post);
    }

    private void addArtistAvatarToPost(final UUID ownerId,
                                       final UUID artistId,
                                       final URI thumbnailUri) {
        postRepository.findAllByOwnerIdAndArtistIdInArtists(ownerId, artistId)
                .forEach(post -> {
                    post.setArtistThumbnailUri(artistId, thumbnailUri);

                    postRepository.save(post);
                });
    }

    private void deleteArtistAvatarFromPost(final UUID ownerId,
                                            final UUID artistId) {
        postRepository.findAllByOwnerIdAndArtistIdInArtists(ownerId, artistId)
                .forEach(post -> {
                    post.setArtistThumbnailUri(artistId, null);

                    postRepository.save(post);
                });
    }

    private void replaceArtistAvatarInPost(final UUID userId, final UUID artistId, final URI thumbnailUri) {
        postRepository.findAllByOwnerIdAndArtistIdInArtists(userId, artistId)
                .forEach(post -> {
                    post.setArtistThumbnailUri(artistId, thumbnailUri);

                    postRepository.save(post);
                });
    }
}
