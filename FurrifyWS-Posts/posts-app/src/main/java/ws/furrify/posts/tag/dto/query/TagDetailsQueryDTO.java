package ws.furrify.posts.tag.dto.query;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.hateoas.RepresentationModel;
import ws.furrify.posts.tag.TagType;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author Skyte
 */
@EqualsAndHashCode(callSuper = true)
@Value
@NonFinal
@ToString
public class TagDetailsQueryDTO extends RepresentationModel<TagDetailsQueryDTO> implements Serializable {
    String value;
    UUID ownerId;

    TagType type;

    ZonedDateTime createDate;
}
