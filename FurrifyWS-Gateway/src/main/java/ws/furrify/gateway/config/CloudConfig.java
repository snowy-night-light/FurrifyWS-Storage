package ws.furrify.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CloudConfig {

    /*
       TODO CURRENTLY BROKEN
     */

    @Bean
    RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/posts/**")
                        .uri("lb://POSTS_SERVICE"))
                .build();
    }
}
