package ws.furrify.posts.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.http.entity.ContentType;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Interceptor adding oauth 2.0 credentials to outgoing request with open feign.
 */
class OpenFeignKeycloakInterceptor implements RequestInterceptor {

    private final static String AUTHORIZATION_HEADER_NAME = "Authorization";
    private final static String AUTHORIZATION_PRE_FIX = "Bearer ";
    private final static String ACCEPT_HEADER_NAME = "Accept";

    @Override
    public void apply(final RequestTemplate requestTemplate) {
        var securityContext = SecurityContextHolder.getContext();
        var authentication = (KeycloakAuthenticationToken)
                securityContext.getAuthentication();

        if (authentication == null) {
            return;
        }

        var token = authentication.getAccount().getKeycloakSecurityContext().getTokenString();

        requestTemplate.header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_PRE_FIX + token);
        requestTemplate.header(ACCEPT_HEADER_NAME, ContentType.APPLICATION_JSON.getMimeType());
    }
}
