package ws.furrify.shared.security;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.UUID;

public abstract class JwtAuthorizationUtils {

    /**
     * Extract userId from JWT token.
     *
     * @param jwtAuthenticationToken JWT token.
     * @return UserId from token.
     */
    public UUID getCurrentUserId(JwtAuthenticationToken jwtAuthenticationToken) {
        return UUID.fromString(
                jwtAuthenticationToken.getToken().getSubject()
        );
    }

}
