package com.zenith.gateway.jwt;

import io.jsonwebtoken.Claims;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * Filter that validates JWT for protected routes. On success, adds
 * X-User-Id and X-User-Type to the request and forwards; otherwise returns 401.
 */
@Component
public class JwtAuthFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_TYPE = "X-User-Type";
    private static final String CLAIM_USER_TYPE = "userType";

    private final GatewayJwtValidator jwtValidator;

    public JwtAuthFilter(GatewayJwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {
        String auth = request.headers().firstHeader(AUTHORIZATION);
        if (auth == null || !auth.startsWith(BEARER_PREFIX)) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = auth.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
        }

        Claims claims;
        try {
            claims = jwtValidator.parseAndValidate(token);
        } catch (Exception e) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userId = claims.getSubject();
        String userType = claims.get(CLAIM_USER_TYPE, String.class);
        if (userId == null) {
            userId = "";
        }
        if (userType == null) {
            userType = "";
        }

        ServerRequest forwardRequest = ServerRequest.from(request)
                .header(HEADER_USER_ID, userId)
                .header(HEADER_USER_TYPE, userType)
                .build();

        return next.handle(forwardRequest);
    }



}
