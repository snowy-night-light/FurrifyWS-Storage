package ws.furrify.posts.post.dto;

import lombok.RequiredArgsConstructor;
import ws.furrify.posts.post.PostEvent;
import ws.furrify.posts.post.PostQueryRepository;
import ws.furrify.posts.post.vo.PostArtist;
import ws.furrify.posts.post.vo.PostAttachment;
import ws.furrify.posts.post.vo.PostMedia;
import ws.furrify.posts.post.vo.PostTag;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Creates PostDTO from PostEvent.
 * `
 *
 * @author Skyte
 */
@RequiredArgsConstructor
public class PostDtoFactory {

    private final PostQueryRepository postQueryRepository;

    public PostDTO from(UUID key, PostEvent postEvent) {
        Instant createDateInstant = postEvent.getData().getCreateDate();
        ZonedDateTime createDate = null;

        if (createDateInstant != null) {
            createDate = createDateInstant.atZone(ZoneId.systemDefault());
        }

        var postId = UUID.fromString(postEvent.getPostId());

        return PostDTO.builder()
                .id(
                        postQueryRepository.getIdByPostId(postId)
                )
                .postId(postId)
                .ownerId(key)
                .title(postEvent.getData().getTitle())
                .description(postEvent.getData().getDescription())
                .tags(
                        postEvent.getData().getTags().stream()
                                .map(postTag -> new PostTag(postTag.getValue(), postTag.getType()))
                                .collect(Collectors.toSet())
                )
                .artists(
                        postEvent.getData().getArtists().stream()
                                .map(postArtist -> {
                                    URI thumbnailUri;
                                    try {
                                        thumbnailUri = new URI(postArtist.getThumbnailUri());
                                    } catch (NullPointerException | URISyntaxException e) {
                                        thumbnailUri = null;
                                    }

                                    return new PostArtist(
                                            UUID.fromString(postArtist.getArtistId()),
                                            postArtist.getPreferredNickname(),
                                            thumbnailUri
                                    );
                                })
                                .collect(Collectors.toSet())
                )
                .mediaSet(
                        postEvent.getData().getMediaSet().stream()
                                .map(postMedia -> {
                                    URI thumbnailUri;
                                    try {
                                        thumbnailUri = new URI(postMedia.getThumbnailUri());
                                    } catch (NullPointerException | URISyntaxException e) {
                                        thumbnailUri = null;
                                    }

                                    URI fileUri;
                                    try {
                                        fileUri = new URI(postMedia.getFileUri());
                                    } catch (NullPointerException | URISyntaxException e) {
                                        throw new IllegalStateException("Invalid file uri received in event.");
                                    }

                                    return new PostMedia(
                                            UUID.fromString(postMedia.getMediaId()),
                                            postMedia.getPriority(),
                                            fileUri,
                                            thumbnailUri,
                                            postMedia.getExtension()
                                    );
                                })
                                .collect(Collectors.toSet())
                )
                .attachments(
                        postEvent.getData().getAttachments().stream()
                                .map(postAttachment -> {

                                    URI fileUri;
                                    try {
                                        fileUri = new URI(postAttachment.getFileUri());
                                    } catch (NullPointerException | URISyntaxException e) {
                                        throw new IllegalStateException("Invalid file uri received in event.");
                                    }

                                    return new PostAttachment(
                                            UUID.fromString(postAttachment.getAttachmentId()),
                                            fileUri,
                                            postAttachment.getFilename(),
                                            postAttachment.getExtension()
                                    );
                                })
                                .collect(Collectors.toSet())
                )
                .createDate(createDate)
                .build();
    }

}
