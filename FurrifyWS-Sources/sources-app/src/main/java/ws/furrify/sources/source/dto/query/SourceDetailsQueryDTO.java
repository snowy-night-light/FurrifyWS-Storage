package ws.furrify.sources.source.dto.query;

import ws.furrify.sources.source.strategy.SourceStrategy;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface SourceDetailsQueryDTO extends Serializable {

    UUID getSourceId();

    UUID getOwnerId();

    HashMap<String, String> getData();

    SourceStrategy getStrategy();

    ZonedDateTime getCreateDate();

}
