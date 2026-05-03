package com.taurustechnology.backend.securities.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.taurustechnology.backend.dtos.TokenResponse;
import com.taurustechnology.backend.models.AppUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${tlf.app.jwtSecret}")
    private String secretKey;
    @Value("${tlf.app.access.expiration.duration.ms}")
    private Long expireAccess;
    @Value("${tlf.app.refresh.expiration.duration.ms}")
    private Long expireRefresh;

    public static final List<String> PATHS = List.of("/login","/error","/api/users/register",
            "/api/tickets/take","/api/auth/refresh");

    // Suppression du static pour une meilleure testabilité

    public final String getSecret() { return secretKey; }

    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    public TokenResponse generateTokens(User user, AppUser appUser, List<String> roles, HttpServletRequest request) {
        Algorithm algorithm = Algorithm.HMAC512(secretKey);
        long now = System.currentTimeMillis();

        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(now + expireAccess))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", roles)
                .withClaim("userId", appUser.getId())
                .sign(algorithm);

        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(now + expireRefresh))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static  boolean isPermittedPath(String path){

        for(String paths : PATHS){
            if(path.equals(paths) || path.startsWith(paths)){
                return true;
            }
        }
        return false;
    }
}