package ws.furrify.sources.source;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.lang.Nullable;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.InvalidDataGivenException;
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
    private HashMap<String, String> data;

    @NonNull
    private SourceStrategy strategy;

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

    /**
     * Update data and strategy.
     * If any of the values are null the current one will be used.
     * Nullable annotation used to suppress false warning when using method in update source adapter.
     *
     * @param data     Nullable data hashmap.
     * @param strategy Nullable strategy.
     */
    void updateData(@Nullable final HashMap<String, String> data,
                    @Nullable final SourceStrategy strategy) {

        // Replace with current values if null
        final HashMap<String, String> finalData = (data != null) ? data : this.data;
        final SourceStrategy finalStrategy = (strategy != null) ? strategy : this.strategy;

        // Ignore warning
        var validationResult = strategy.validate(finalData);

        if (!validationResult.isValid()) {
            throw new InvalidDataGivenException(Errors.VALIDATION_FAILED.getErrorMessage(
                    strategy.getClass().getSimpleName(),
                    validationResult.getReason()
            ));
        }

        this.strategy = finalStrategy;
        this.data = finalData;
    }
}
