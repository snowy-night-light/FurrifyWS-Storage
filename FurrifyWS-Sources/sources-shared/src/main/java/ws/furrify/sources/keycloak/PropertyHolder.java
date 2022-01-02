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

    public static String AUTH_SERVER;
    public static String REALM;

    @Value("${keycloak.auth-server-url}")
    public void setAuthServer(String authServer) {
        PropertyHolder.AUTH_SERVER = authServer;
    }

    @Value("${keycloak.realm}")
    public void setRealm(String realm) {
        PropertyHolder.REALM = realm;
    }
}
