package com.taurustechnology.backend.configs.securities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.taurustechnology.backend.dtos.Tokens;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Utility class for JWT token operations.
 */
@Component
public class JwtUtil {

    @Value("${signature.key}")
    private String secretKey;

    @Value("${access.expired.time}")
    private Long expireAccess;

    @Value("${refresh.expired.time}")
    private Long expireRefresh;

    public static String SECRET;
    public static Long EXPIRE_ACCESS;
    public static Long EXPIRE_REFRESH;

    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    // List of permitted paths for authentication bypass or exceptions
    public static final List<String> PATHS = List.of(
            "/login",
            "/logout",
            "/auth/refresh-token",
            "/swagger-ui",
            "/v3/api-docs",
            "/sse",
            "/mcp/message",
            "/check-role-controller",
            "/auth/users/find"
    );

    /**
     * Initializes static variables after dependency injection.
     */
    @PostConstruct
    private void init() {
        SECRET = secretKey;
        EXPIRE_ACCESS = expireAccess;
        EXPIRE_REFRESH = expireRefresh;
    }

    /**
     * Generates access and refresh JWT tokens for a given user.
     *
     * @param user      the Spring Security user
     * @param roles     the list of user's roles
     * @param algorithm the JWT signing algorithm
     * @param request   the HTTP servlet request
     * @return a Tokens object containing access and refresh tokens
     */
    public static Tokens generateTokens(User user, List<String> roles, Algorithm algorithm, HttpServletRequest request) {
        long currentTimeMillis = System.currentTimeMillis();

        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(currentTimeMillis + EXPIRE_ACCESS))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", roles)
                .sign(algorithm);

        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(currentTimeMillis + EXPIRE_REFRESH))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);

        return Tokens.builder()
                .time(currentTimeMillis)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Checks if the given path is in the list of permitted paths.
     *
     * @param path the URI path to check
     * @return true if the path is permitted, false otherwise
     */
    public static boolean isPermittedPath(String path) {
        for (String permittedPath : PATHS) {
            if (path.equals(permittedPath) || path.startsWith(permittedPath)) {
                return true;
            }
        }
        return false;
    }
}