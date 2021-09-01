package ws.furrify.sources.source;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import ws.furrify.sources.source.strategy.SourceStrategy;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
class Source {
    private final Long id;
    @NonNull
    private final UUID sourceId;
    @NonNull
    private final UUID ownerId;

    @NonNull
    private final HashMap<String, String> data;

    @NonNull
    private final SourceStrategy strategy;

    private final ZonedDateTime createDate;

    static Source restore(SourceSnapshot sourceSnapshot) {
        return new Source(
                sourceSnapshot.getId(),
                sourceSnapshot.getSourceId(),
                sourceSnapshot.getOwnerId(),
                new HashMap<>(sourceSnapshot.getData()),
                sourceSnapshot.getStrategy(),
                sourceSnapshot.getCreateDate()
        );
    }

    SourceSnapshot getSnapshot() {
        return SourceSnapshot.builder()
                .id(id)
                .sourceId(sourceId)
                .ownerId(ownerId)
                .data(new HashMap<>(data))
                .strategy(strategy)
                .createDate(createDate)
                .build();
    }

}
