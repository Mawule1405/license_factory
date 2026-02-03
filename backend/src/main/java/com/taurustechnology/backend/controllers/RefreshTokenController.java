package com.taurustechnology.backend.controllers;

import com.taurustechnology.backend.configs.securities.JwtUtil;
import com.taurustechnology.backend.dtos.RefreshTokenRequest;
import com.taurustechnology.backend.dtos.TokenResponse;
import com.taurustechnology.backend.services.impl.TokenRefreshService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controller for handling JWT refresh token operations.
 */
@RestController
@RequestMapping("/api/refresh-token")
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenController {

    private final TokenRefreshService tokenRefreshService;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Endpoint to securely refresh JWT tokens.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException in case of I/O error
     */
    @PostMapping
    public void refreshToken(
            @RequestBody RefreshTokenRequest tokenRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");


        String refreshToken = tokenRequest.refreshToken();

        try {
            // Call service to refresh token with built-in security checks
            TokenResponse tokenResponse = tokenRefreshService.refreshToken(refreshToken, request);

            // Success response
            response.setStatus(HttpServletResponse.SC_OK);
            OBJECT_MAPPER.writeValue(response.getOutputStream(), tokenResponse);

        } catch (SecurityException ex) {
            // Security errors (user disconnected, account disabled, etc.)
            log.warn("Security exception during token refresh: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            Map<String, Object> errorResponse = Map.of(
                    "success", false,
                    "error_message", ex.getMessage(),
                    "error_type", "SECURITY_ERROR",
                    "timestamp", LocalDateTime.now(),
                    "action_required", "Please login again"
            );
            OBJECT_MAPPER.writeValue(response.getOutputStream(), errorResponse);

        } catch (RuntimeException ex) {
            // Token errors (expired, invalid, etc.)
            log.warn("Token exception during token refresh: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);

            Map<String, Object> errorResponse = Map.of(
                    "success", false,
                    "error_message", ex.getMessage(),
                    "error_type", "TOKEN_ERROR",
                    "timestamp", LocalDateTime.now()
            );
            OBJECT_MAPPER.writeValue(response.getOutputStream(), errorResponse);

        } catch (Exception ex) {
            // General errors
            log.error("Internal error during token refresh", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            Map<String, Object> errorResponse = Map.of(
                    "success", false,
                    "error_message", "Internal server error during token refresh",
                    "error_type", "INTERNAL_ERROR",
                    "timestamp", LocalDateTime.now()
            );
            OBJECT_MAPPER.writeValue(response.getOutputStream(), errorResponse);
        }
    }
}