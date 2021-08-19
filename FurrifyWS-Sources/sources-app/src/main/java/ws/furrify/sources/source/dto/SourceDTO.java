package ws.furrify.sources.source.dto;

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
public class SourceDTO {
     Long id;

     UUID sourceId;
     UUID ownerId;

     ZonedDateTime createDate;
}
