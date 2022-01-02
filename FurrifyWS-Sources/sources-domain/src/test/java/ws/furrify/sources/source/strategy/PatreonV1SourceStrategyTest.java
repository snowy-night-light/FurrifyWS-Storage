package ws.furrify.sources.source.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PatreonV1SourceStrategyTest {

    private UUID id;
    private HashMap<String, String> data;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        data = new HashMap<>();
    }

    @Test
    @DisplayName("Validate")
    void validate() {
        // Given
        PatreonV1SourceStrategy patreonV1SourceStrategy = new PatreonV1SourceStrategy();
        // When
        // Then
        assertTrue(patreonV1SourceStrategy.validate(data).isValid(), "Validation failed with correct parameters.");
    }
}