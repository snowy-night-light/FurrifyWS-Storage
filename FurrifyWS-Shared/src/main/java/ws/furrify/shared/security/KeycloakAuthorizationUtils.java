package ws.furrify.shared.security;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;

import java.util.UUID;

public abstract class KeycloakAuthorizationUtils {

    /**
     * Extract userId from keycloak token.
     *
     * @param keycloakAuthenticationToken Keycloak token.
     * @return UserId from token.
     */
    public UUID getCurrentUserId(KeycloakAuthenticationToken keycloakAuthenticationToken) {
        return UUID.fromString(
                keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken().getSubject()
        );
    }

}
