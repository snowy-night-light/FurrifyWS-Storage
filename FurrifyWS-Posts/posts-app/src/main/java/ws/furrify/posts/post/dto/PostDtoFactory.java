package ws.furrify.posts.post.dto;

import lombok.RequiredArgsConstructor;
import ws.furrify.posts.post.PostEvent;
import ws.furrify.posts.post.PostQueryRepository;
import ws.furrify.posts.post.vo.PostArtist;
import ws.furrify.posts.post.vo.PostTag;

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
                                .map(postArtist -> new PostArtist(
                                        UUID.fromString(postArtist.getArtistId()), postArtist.getPreferredNickname()
                                ))
                                .collect(Collectors.toSet())
                )
                .createDate(createDate)
                .build();
    }

}
