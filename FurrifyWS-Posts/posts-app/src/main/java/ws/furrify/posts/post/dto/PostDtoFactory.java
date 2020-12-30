package ws.furrify.posts.post.dto;

import ws.furrify.posts.PostEvent;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

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
                .id(postEvent.getPostId())
                .postId(UUID.fromString(postEvent.getPostUUID()))
                .ownerId(key)
                .title(postEvent.getData().getTitle())
                .description(postEvent.getData().getDescription())
                .createDate(createDate)
                .build();
    }

}
