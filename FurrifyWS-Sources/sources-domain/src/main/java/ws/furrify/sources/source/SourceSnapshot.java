package ws.furrify.sources.source;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ws.furrify.sources.source.strategy.SourceStrategy;
import ws.furrify.sources.source.vo.SourceOriginType;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@EqualsAndHashCode
@ToString
@Getter(value = PACKAGE)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Builder(access = PACKAGE)
class SourceSnapshot {
    private Long id;

    private UUID originId;
    private UUID postId;
    private UUID sourceId;
    private UUID ownerId;

    private HashMap<String, String> data;

    private SourceStrategy strategy;

    private SourceOriginType originType;

    private ZonedDateTime createDate;
}