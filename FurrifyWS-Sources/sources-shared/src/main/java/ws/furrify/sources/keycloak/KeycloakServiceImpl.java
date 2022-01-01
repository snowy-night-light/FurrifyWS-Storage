package ws.furrify.sources.keycloak;

import feign.Feign;
import feign.FeignException;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.extern.slf4j.Slf4j;
import ws.furrify.shared.exception.HttpStatus;
import ws.furrify.sources.keycloak.dto.KeycloakIdpTokenQueryDTO;

/**
 * Implementation of Keycloak communication service using Feign.
 *
 * @author Skyte
 */
@Slf4j
public class KeycloakServiceImpl implements KeycloakServiceClient {

    private final KeycloakServiceClient keycloakServiceClient;

    public KeycloakServiceImpl() {
        this.keycloakServiceClient = Feign.builder()
                .client(new OkHttpClient())
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .logger(new Slf4jLogger(KeycloakServiceClient.class))
                .logLevel(Logger.Level.FULL)
                .target(KeycloakServiceClient.class, "${keycloak.auth-server-url}");
    }

    @Bulkhead(name = "getKeycloakIdentityProviderToken", fallbackMethod = "getKeycloakIdentityProviderTokenFallback")
    @Override
    public KeycloakIdpTokenQueryDTO getKeycloakIdentityProviderToken(final String realm, final String broker) {
        return keycloakServiceClient.getKeycloakIdentityProviderToken(realm, broker);
    }

    private KeycloakIdpTokenQueryDTO getKeycloakIdentityProviderTokenFallback(Throwable throwable) {
        var exception = (FeignException) throwable;

        HttpStatus status = HttpStatus.of(exception.status());

        log.error("Keycloak identity provider endpoint returned status " + status.getStatus() + ".");

        throw exception;
    }
}
