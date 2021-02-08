package ws.furrify.posts.post.dto;

import ws.furrify.posts.PostEvent;
import ws.furrify.posts.post.vo.PostArtist;
import ws.furrify.posts.post.vo.PostTag;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Creates PostDTO from PostEvent.
 * `
 *
 * @author Skyte
 */
public class PostDtoFactory {

    public PostDTO from(UUID key, PostEvent postEvent) {
        Long createDateMillis = postEvent.getData().getCreateDate();
        ZonedDateTime createDate = null;

        if (createDateMillis != null) {
            createDate = new Date(createDateMillis).toInstant().atZone(ZoneId.systemDefault());
        }

        return PostDTO.builder()
                .id(postEvent.getId())
                .postId(UUID.fromString(postEvent.getPostId()))
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
