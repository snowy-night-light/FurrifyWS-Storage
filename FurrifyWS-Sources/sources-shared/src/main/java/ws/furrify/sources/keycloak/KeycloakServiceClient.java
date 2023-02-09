package ws.furrify.sources.keycloak;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import ws.furrify.sources.keycloak.dto.KeycloakIdpTokenQueryDTO;

/**
 * Communication interface with keycloak authorization server.
 *
 * @author Skyte
 */
public interface KeycloakServiceClient {
    /**
     * Get Keycloak identity provider token.
     *
     * @param bearerToken Bearer token to use for authentication.
     * @param broker      Identity provider unique name.
     * @return Keycloak identity provider token information.
     */
    @Headers("Authorization: {bearerToken}")
    @RequestLine("GET /broker/{broker}/token")
    KeycloakIdpTokenQueryDTO getKeycloakIdentityProviderToken(@Param("bearerToken") String bearerToken, @Param("broker") String broker);
}
