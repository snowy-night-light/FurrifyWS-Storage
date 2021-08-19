package ws.furrify.sources.source;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.time.ZonedDateTime;
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

    private final ZonedDateTime createDate;

    static Source restore(SourceSnapshot sourceSnapshot) {
        return new Source(
                sourceSnapshot.getId(),
                sourceSnapshot.getSourceId(),
                sourceSnapshot.getOwnerId(),
                sourceSnapshot.getCreateDate()
        );
    }

    SourceSnapshot getSnapshot() {
        return SourceSnapshot.builder()
                .id(id)
                .sourceId(sourceId)
                .ownerId(ownerId)
                .createDate(createDate)
                .build();
    }

}
