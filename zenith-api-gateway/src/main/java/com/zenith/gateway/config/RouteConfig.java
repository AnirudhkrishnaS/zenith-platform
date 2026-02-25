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

    @Bean
    public RouterFunction<ServerResponse> userServiceRoutes(JwtAuthFilter jwtAuthFilter) {
        // /api/auth/** – register (POST), login (POST) – no JWT required
        RouterFunction<ServerResponse> authRoute = route("user-service-auth")
                .GET("/api/auth/**", http())
                .POST("/api/auth/**", http())
                .before(uri(USER_SERVICE_URI))
                .build();

        // /api/users/** – getMe (GET), updateMe (PUT) – JWT required; adds X-User-Id, X-User-Type
        RouterFunction<ServerResponse> usersRoute = route("user-service-users")
                .GET("/api/users/**", http())
                .PUT("/api/users/**", http())
                .before(uri(USER_SERVICE_URI))
                .build()
                .filter(jwtAuthFilter);

        return authRoute.and(usersRoute);
    }
}
