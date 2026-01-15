package com.taurustechnology.backend.services.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.taurustechnology.backend.configs.securities.JwtUtil;
import com.taurustechnology.backend.dtos.TokenResponse;
import com.taurustechnology.backend.dtos.Tokens;
import com.taurustechnology.backend.entities.AppRole;
import com.taurustechnology.backend.entities.AppUser;
import com.taurustechnology.backend.services.AppUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsible for refreshing JWT tokens with enhanced security validations.
 * Handles token refresh operations and security checks.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TokenRefreshService {

    private final AppUserService appUserService;

    /**
     * Refreshes a JWT token with enhanced security validations.
     *
     * @param refreshToken the refresh token to validate and use for refresh
     * @param request the HTTP servlet request for context information
     * @return TokenResponse containing new access and refresh tokens
     * @throws SecurityException if security validations fail
     * @throws RuntimeException if token is invalid or other errors occur
     */
    public TokenResponse refreshToken(String refreshToken, HttpServletRequest request) {
        log.info("Attempting to refresh token");

        try {
            // 1. VALIDATE REFRESH TOKEN WITH SECURITY CHECKS
            if (isRefreshTokenExpired(refreshToken)) {
                log.warn("Refresh token is expired");
                throw new SecurityException("Refresh token is expired");
            }

            // 2. EXTRACT AND VALIDATE USER PROFILE
            String username = extractUsernameFromRefreshToken(refreshToken);
            if (username == null) {
                log.warn("Invalid refresh token - cannot extract username");
                throw new SecurityException("Invalid refresh token");
            }

            AppUser appUser = appUserService.findByUsernameOrEmail(username, username);
            if (appUser == null) {
                log.warn("User profile not found for username: {}", username);
                throw new SecurityException("User profile not found");
            }

            // 3. CRITICAL SECURITY VALIDATIONS
            if (!isUserEligibleForLogin(appUser)) {
                log.warn("User account is not eligible for login: {}", username);
                throw new SecurityException("User account is disabled or deleted");
            }

            // 4. GENERATE NEW TOKENS
            Algorithm algorithm = Algorithm.HMAC512(JwtUtil.SECRET);

            // Extract roles
            List<String> roles = extractUserRoles(appUser);

            // Create Spring Security authorities
            Collection<GrantedAuthority> authorities = createAuthorities(roles);

            // Create Spring Security User object
            User user = new User(appUser.getUsername(), appUser.getPasswordHash(), authorities);

            // Generate new tokens
            Tokens tokens = JwtUtil.generateTokens(user, roles, algorithm, request);

            log.info("Tokens successfully refreshed for user: {}", username);

            // 5. CREATE RESPONSE

            return new TokenResponse(tokens.getAccessToken(), tokens.getRefreshToken());

        } catch (SecurityException e) {
            log.error("Security exception during token refresh: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error during token refresh: {}", e.getMessage(), e);
            throw new RuntimeException("Token refresh failed: " + e.getMessage(), e);
        }
    }

    /**
     * Checks if a user is eligible for login based on account status.
     *
     * @param appUser the user entity to check
     * @return true if user can login, false otherwise
     */
    private boolean isUserEligibleForLogin(AppUser appUser) {
        return appUser.isActivated() && !appUser.isDeleted();
    }

    /**
     * Extracts role names from the user entity.
     *
     * @param appUser the user entity containing roles
     * @return list of role names
     */
    private List<String> extractUserRoles(AppUser appUser) {
        if (appUser.getAppRoles() == null || appUser.getAppRoles().isEmpty()) {
            log.warn("User {} has no roles assigned", appUser.getUsername());
            return List.of("USER"); // Default role
        }

        return appUser.getAppRoles().stream()
                .map(AppRole::getName)
                .filter(roleName -> roleName != null && !roleName.trim().isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Creates Spring Security authorities from role names.
     *
     * @param roles the list of role names
     * @return collection of GrantedAuthority objects
     */
    private Collection<GrantedAuthority> createAuthorities(List<String> roles) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
            // Add ROLE_ prefix for Spring Security's hasRole() method
            if (!role.startsWith("ROLE_")) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
        }

        return authorities;
    }

    /**
     * Extracts username from a refresh token.
     *
     * @param refreshToken the refresh token to decode
     * @return the username (subject) from the token, or null if invalid
     */
    public String extractUsernameFromRefreshToken(String refreshToken) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(JwtUtil.SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(refreshToken);

            return decodedJWT.getSubject();
        } catch (Exception e) {
            log.debug("Failed to extract username from refresh token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Checks if a refresh token is expired.
     *
     * @param refreshToken the token to check
     * @return true if expired, false if valid
     */
    public boolean isRefreshTokenExpired(String refreshToken) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(JwtUtil.SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(refreshToken);

            return decodedJWT.getExpiresAt().before(new Date());
        } catch (Exception e) {
            log.debug("Refresh token validation failed: {}", e.getMessage());
            return true; // Consider invalid tokens as expired
        }
    }

    /**
     * Validates a refresh token for expiration and signature.
     *
     * @param refreshToken the token to validate
     * @return true if valid and not expired, false otherwise
     */
    public boolean isValidRefreshToken(String refreshToken) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(JwtUtil.SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(refreshToken);

            return !decodedJWT.getExpiresAt().before(new Date());
        } catch (Exception e) {
            log.debug("Refresh token validation failed: {}", e.getMessage());
            return false;
        }
    }
}