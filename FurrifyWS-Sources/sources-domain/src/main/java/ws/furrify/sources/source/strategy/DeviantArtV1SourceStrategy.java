package ws.furrify.sources.source.strategy;

import lombok.RequiredArgsConstructor;
import ws.furrify.sources.keycloak.KeycloakServiceClient;
import ws.furrify.sources.keycloak.KeycloakServiceClientImpl;
import ws.furrify.sources.keycloak.PropertyHolder;
import ws.furrify.sources.providers.deviantart.DeviantArtServiceClient;
import ws.furrify.sources.providers.deviantart.DeviantArtServiceClientImpl;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtDeviationQueryDTO;

import java.util.HashMap;

/**
 * Version 1 of Deviant Art Strategy.
 * Represents DeviantArt.com website for artists and content.
 *
 * @author sky
 */
@RequiredArgsConstructor
public class DeviantArtV1SourceStrategy implements SourceStrategy {

    public final static String BROKER_ID = "deviantart";
    private final static String DEVIATION_ID_FIELD = "id";
    private final KeycloakServiceClient keycloakService = new KeycloakServiceClientImpl();
    private final DeviantArtServiceClient deviantArtService = new DeviantArtServiceClientImpl();

    @Override
    public ValidationResult validate(final HashMap<String, String> data) {
        if (data.get(DEVIATION_ID_FIELD) == null) {
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
}
