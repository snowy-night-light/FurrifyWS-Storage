package ws.furrify.tags.tag.dto;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import ws.furrify.tags.tag.vo.TagType;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author Skyte
 */
@Builder(toBuilder = true)
@Value
@ToString
public class TagDTO {

    Long id;

    String value;
    UUID ownerId;

    TagType type;

    ZonedDateTime createDate;
}

