package ws.furrify.tags.tag.dto.query;

import ws.furrify.tags.tag.vo.TagType;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface TagDetailsQueryDTO extends Serializable {
    String getTitle();

    String getDescription();

    String getValue();

    UUID getOwnerId();

    TagType getType();

    ZonedDateTime getCreateDate();
}
