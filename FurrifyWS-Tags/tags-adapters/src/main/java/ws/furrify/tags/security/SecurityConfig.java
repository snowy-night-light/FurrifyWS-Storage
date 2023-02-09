package ws.furrify.tags.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ws.furrify.shared.security.OpenFeignKeycloakInterceptor;

@Configuration
@EnableMethodSecurity
class SecurityConfig {

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    OpenFeignKeycloakInterceptor openFeignKeycloakInterceptor() {
        return new OpenFeignKeycloakInterceptor();
    }

}