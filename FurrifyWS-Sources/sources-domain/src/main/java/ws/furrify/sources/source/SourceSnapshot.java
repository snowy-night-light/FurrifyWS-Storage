package ws.furrify.sources.source;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;
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

    private UUID sourceId;
    private UUID ownerId;

    private ZonedDateTime createDate;
}