package ws.furrify.sources.source.dto.query;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface SourceDetailsQueryDTO extends Serializable {

    UUID getSourceId();

    UUID getOwnerId();

    ZonedDateTime getCreateDate();

}
