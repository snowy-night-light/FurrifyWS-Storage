package ws.furrify.posts.post.dto.query;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.hateoas.RepresentationModel;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author Skyte
 */
@EqualsAndHashCode(callSuper = true)
@Value
@NonFinal
@ToString
public class PostDetailsQueryDTO extends RepresentationModel<PostDetailsQueryDTO> implements PostQueryDTO {

    UUID postId;
    UUID ownerId;

    String title;
    String description;

    ZonedDateTime createDate;
}
