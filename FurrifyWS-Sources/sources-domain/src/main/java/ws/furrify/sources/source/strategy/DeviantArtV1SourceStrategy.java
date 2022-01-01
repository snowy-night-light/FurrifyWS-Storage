package ws.furrify.sources.source.strategy;

import lombok.RequiredArgsConstructor;
import ws.furrify.sources.keycloak.KeycloakServiceImpl;
import ws.furrify.sources.providers.deviantart.DeviantArtServiceImpl;
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
    private final KeycloakServiceImpl keycloakService = new KeycloakServiceImpl();
    private final DeviantArtServiceImpl deviantArtService = new DeviantArtServiceImpl();

    @Override
    public ValidationResult validate(final HashMap<String, String> data) {
        if (data.get(DEVIATION_ID_FIELD) == null) {
            return ValidationResult.invalid("Deviation id is required.");
        }

        // TODO Load from application yaml
        String bearerToken = "Bearer " + keycloakService.getKeycloakIdentityProviderToken("dev", BROKER_ID);

        DeviantArtDeviationQueryDTO deviationQueryDTO =
                deviantArtService.getDeviation(bearerToken, data.get(DEVIATION_ID_FIELD));
        if (deviationQueryDTO == null) {
            return ValidationResult.invalid("Deviation not found.");
        }

        return ValidationResult.valid();
    }
}
