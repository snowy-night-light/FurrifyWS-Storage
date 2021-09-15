package ws.furrify.sources.source.dto;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import ws.furrify.sources.source.strategy.SourceStrategy;
import ws.furrify.sources.source.vo.SourceOriginType;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author Skyte
 */
@Builder(toBuilder = true)
@Value
@ToString
public class SourceDTO {
    Long id;

    UUID originId;
    UUID postId;
    UUID sourceId;
    UUID ownerId;

    HashMap<String, String> data;

    SourceStrategy strategy;

    SourceOriginType originType;

    ZonedDateTime createDate;
}
