package com.zenith.gateway.config;

import com.zenith.gateway.jwt.JwtAuthFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class RouteConfig {

    private static final String USER_SERVICE_URI = "http://localhost:8081";
    private static final String STORE_SERVICE_URI = "http://localhost:8082";

    @Bean
    public RouterFunction<ServerResponse> userServiceRoutes(JwtAuthFilter jwtAuthFilter) {
        RouterFunction<ServerResponse> authRoute = route("user-service-auth")
                .GET("/api/auth/**", http())
                .POST("/api/auth/**", http())
                .before(uri(USER_SERVICE_URI))
                .build();

        RouterFunction<ServerResponse> usersRoute = route("user-service-users")
                .GET("/api/users/**", http())
                .PUT("/api/users/**", http())
                .before(uri(USER_SERVICE_URI))
                .build()
                .filter(jwtAuthFilter);

        RouterFunction<ServerResponse> storesRoute = route("store-service")
                .GET("/api/stores/**", http())
                .POST("/api/stores/**", http())
                .PUT("/api/stores/**", http())
                .DELETE("/api/stores/**", http())
                .before(uri(STORE_SERVICE_URI))
                .build()
                .filter(jwtAuthFilter);

        return authRoute.and(usersRoute).and(storesRoute);
    }
}
