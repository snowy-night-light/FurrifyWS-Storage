package ws.furrify.shared.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Interceptor adding OAuth 2.0 credentials to outgoing request with open feign.
 *
 * @author Skyte
 */
public class OpenFeignKeycloakInterceptor implements RequestInterceptor {

    private final static String AUTHORIZATION_HEADER_NAME = "Authorization";

    @Override
    public void apply(final RequestTemplate requestTemplate) {
        var requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (requestAttributes == null) {
            return;
        }

        var token = requestAttributes.getRequest().getHeader("Authorization");

        requestTemplate.header(AUTHORIZATION_HEADER_NAME, token);
    }
}
