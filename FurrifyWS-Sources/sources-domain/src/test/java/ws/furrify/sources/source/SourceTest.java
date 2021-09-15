package ws.furrify.sources.source;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.sources.source.strategy.DefaultSourceStrategy;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class SourceTest {

    // Mocked in beforeAll()
    private static SourceRepository sourceRepository;

    private SourceSnapshot sourceSnapshot;
    private Source source;

    @BeforeEach
    void setUp() {
        sourceSnapshot = SourceSnapshot.builder()
                .id(0L)
                .sourceId(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .strategy(new DefaultSourceStrategy())
                .data(new HashMap<>())
                .createDate(ZonedDateTime.now())
                .build();

        source = Source.restore(sourceSnapshot);
    }

    @BeforeAll
    static void beforeAll() {
        sourceRepository = mock(SourceRepository.class);
    }

    @Test
    @DisplayName("Source restore from snapshot")
    void restore() {
        // Given sourceSnapshot
        // When restore() method called
        Source source = Source.restore(sourceSnapshot);
        // Then restore all values into aggregate and back to snapshot without losing data
        assertEquals(sourceSnapshot, source.getSnapshot(), "Data was lost in restore call.");
    }

    @Test
    @DisplayName("Get snapshot from source")
    void getSnapshot() {
        // Given source
        // When getSnapshot() method called
        SourceSnapshot sourceSnapshot = source.getSnapshot();
        // Then get snapshot of current data in aggregate
        assertEquals(this.sourceSnapshot, sourceSnapshot, "Data was lost in snapshot.");
    }

}