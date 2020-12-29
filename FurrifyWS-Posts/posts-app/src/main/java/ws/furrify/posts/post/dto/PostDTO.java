package ws.furrify.posts.post.dto;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author Skyte
 */
@Builder(toBuilder = true)
@Value
@ToString
public class PostDTO {

    Long id;

    UUID postId;
    UUID ownerId;

    String title;
    String description;

    ZonedDateTime createDate;

}
