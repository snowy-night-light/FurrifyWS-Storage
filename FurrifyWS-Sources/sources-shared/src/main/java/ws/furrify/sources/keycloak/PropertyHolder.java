package ws.furrify.sources.keycloak;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring property holder.
 *
 * @author sky
 */
@RestController
public class PropertyHolder {

    public static String ISSUER_URI;

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    public void setAuthServer(String authServer) {
        PropertyHolder.ISSUER_URI = authServer;
    }
}
