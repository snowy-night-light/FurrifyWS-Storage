package ws.furrify.sources.source.dto.query;

import ws.furrify.shared.vo.SourceOriginType;
import ws.furrify.sources.source.strategy.SourceStrategy;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface SourceDetailsQueryDTO extends Serializable {

    UUID getOriginId();

    UUID getSourceId();

    UUID getPostId();

    UUID getOwnerId();

    HashMap<String, String> getData();

    SourceStrategy getStrategy();

    SourceOriginType getOriginType();

    ZonedDateTime getCreateDate();

}
