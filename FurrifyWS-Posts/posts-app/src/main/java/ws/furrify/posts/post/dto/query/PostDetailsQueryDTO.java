package ws.furrify.posts.post.dto.query;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.hateoas.RepresentationModel;
import ws.furrify.posts.post.vo.PostTag;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * @author Skyte
 */
@EqualsAndHashCode(callSuper = true)
@Value
@NonFinal
@ToString
public class PostDetailsQueryDTO extends RepresentationModel<PostDetailsQueryDTO> implements Serializable {

    UUID postId;
    UUID ownerId;

    String title;
    String description;

    Set<PostTag> tags;

    ZonedDateTime createDate;
}
