package ws.furrify.sources.source.strategy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.sources.keycloak.KeycloakServiceClient;
import ws.furrify.sources.keycloak.dto.KeycloakIdpTokenQueryDTO;
import ws.furrify.sources.providers.deviantart.DeviantArtServiceClient;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtDeviationQueryDTO;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DeviantArtV1SourceStrategyTest {

    private static KeycloakServiceClient keycloakServiceClient;
    private static DeviantArtServiceClient deviantArtServiceClient;

    private UUID id;
    private HashMap<String, String> data;

    @BeforeAll
    static void beforeAll() {
        keycloakServiceClient = mock(KeycloakServiceClient.class);
        deviantArtServiceClient = mock(DeviantArtServiceClient.class);
    }

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        data = new HashMap<>();
    }

    @Test
    @DisplayName("Validate")
    void validate() {
        // Given
        DeviantArtV1SourceStrategy deviantArtV1SourceStrategy = new DeviantArtV1SourceStrategy();
        // When
        var deviantArtResponse = new DeviantArtDeviationQueryDTO();
        deviantArtResponse.setDeviationId(id.toString());

        when(keycloakServiceClient.getKeycloakIdentityProviderToken(any(), any(), any())).thenReturn(new KeycloakIdpTokenQueryDTO());
        when(deviantArtServiceClient.getDeviation(any(), any())).thenReturn(deviantArtResponse);
        // Then
        assertTrue(deviantArtV1SourceStrategy.validateMedia(data).isValid(), "Validation failed with correct parameters.");
    }

    @Test
    @DisplayName("Validate with empty id property")
    void validate2() {
        // Given
        DeviantArtV1SourceStrategy deviantArtV1SourceStrategy = new DeviantArtV1SourceStrategy();
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateMedia(data).isValid(), "Validation accepted empty id.");
    }

    @Test
    @DisplayName("Validate with non existing id")
    void validate3() {
        // Given
        DeviantArtV1SourceStrategy deviantArtV1SourceStrategy = new DeviantArtV1SourceStrategy();
        // When
        when(keycloakServiceClient.getKeycloakIdentityProviderToken(any(), any(), any())).thenReturn(new KeycloakIdpTokenQueryDTO());
        when(deviantArtServiceClient.getDeviation(any(), any())).thenReturn(null);
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateMedia(data).isValid(), "Validation accepted empty id.");
    }

}