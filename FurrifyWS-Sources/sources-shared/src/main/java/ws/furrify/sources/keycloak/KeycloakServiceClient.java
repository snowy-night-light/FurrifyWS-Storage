package ws.furrify.sources.keycloak;

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
     * @param broker Identity provider unique name.
     * @param realm  Keycloak realm.
     * @return Keycloak identity provider token information.
     */
    @RequestLine("GET /realms/{realm}/broker/{broker}/token")
    KeycloakIdpTokenQueryDTO getKeycloakIdentityProviderToken(@Param("realm") String realm, @Param("broker") String broker);
}
