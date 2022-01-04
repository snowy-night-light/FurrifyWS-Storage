package ws.furrify.sources.source.strategy;

import ws.furrify.sources.keycloak.KeycloakServiceClient;
import ws.furrify.sources.keycloak.KeycloakServiceClientImpl;
import ws.furrify.sources.keycloak.PropertyHolder;
import ws.furrify.sources.providers.deviantart.DeviantArtServiceClient;
import ws.furrify.sources.providers.deviantart.DeviantArtServiceClientImpl;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtDeviationQueryDTO;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtUserQueryDTO;

import java.util.HashMap;

/**
 * Version 1 of Deviant Art Strategy.
 * Represents DeviantArt.com website for artists and content.
 *
 * @author sky
 */
public class DeviantArtV1SourceStrategy implements SourceStrategy {

    public final static String BROKER_ID = "deviantart";

    private final static String DEVIATION_ID_FIELD = "id";
    private final static String USERNAME_FIELD = "username";

    private final KeycloakServiceClient keycloakService;
    private final DeviantArtServiceClient deviantArtService;

    public DeviantArtV1SourceStrategy() {
        this.keycloakService = new KeycloakServiceClientImpl();
        this.deviantArtService = new DeviantArtServiceClientImpl();
    }

    public DeviantArtV1SourceStrategy(final KeycloakServiceClient keycloakService, final DeviantArtServiceClient deviantArtService) {
        this.keycloakService = keycloakService;
        this.deviantArtService = deviantArtService;
    }

    @Override
    public ValidationResult validateMedia(final HashMap<String, String> data) {
        if (data.get(DEVIATION_ID_FIELD) == null || data.get(DEVIATION_ID_FIELD).isBlank()) {
            return ValidationResult.invalid("Deviation id is required.");
        }

        String providerBearerToken = "Bearer " + keycloakService.getKeycloakIdentityProviderToken(null, PropertyHolder.REALM, BROKER_ID).getAccessToken();

        DeviantArtDeviationQueryDTO deviationQueryDTO =
                deviantArtService.getDeviation(providerBearerToken, data.get(DEVIATION_ID_FIELD));
        if (deviationQueryDTO == null) {
            return ValidationResult.invalid("Deviation not found.");
        }

        return ValidationResult.valid();
    }

    @Override
    public ValidationResult validateUser(final HashMap<String, String> data) {
        if (data.get(USERNAME_FIELD) == null || data.get(USERNAME_FIELD).isBlank()) {
            return ValidationResult.invalid("Username is required.");
        }

        String providerBearerToken = "Bearer " + keycloakService.getKeycloakIdentityProviderToken(null, PropertyHolder.REALM, BROKER_ID).getAccessToken();

        DeviantArtUserQueryDTO userQueryDTO =
                deviantArtService.getUser(providerBearerToken, data.get(USERNAME_FIELD));
        if (userQueryDTO == null) {
            return ValidationResult.invalid("User not found.");
        }

        return ValidationResult.valid();
    }

    @Override
    public ValidationResult validateAttachment(final HashMap<String, String> data) {
        return validateMedia(data);
    }
}
