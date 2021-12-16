package ws.furrify.posts.security;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ws.furrify.shared.security.OpenFeignKeycloakInterceptor;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig {

    /**
     * Needs to be here, for whatever reason it cannot be placed in KeycloakConfig class cause framework doesn't like it.
     */
    @Bean
    KeycloakSpringBootConfigResolver keycloakSpringBootConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    OpenFeignKeycloakInterceptor openFeignKeycloakInterceptor() {
        return new OpenFeignKeycloakInterceptor();
    }

}