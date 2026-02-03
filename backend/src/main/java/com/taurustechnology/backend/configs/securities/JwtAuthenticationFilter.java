package com.taurustechnology.backend.configs.securities;

import com.auth0.jwt.algorithms.Algorithm;

import com.taurustechnology.backend.dtos.TokenResponse;
import com.taurustechnology.backend.dtos.Tokens;
import com.taurustechnology.backend.dtos.UserRequest;
import com.taurustechnology.backend.entities.AppUser;
import com.taurustechnology.backend.services.AppUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * JWT Authentication Filter for handling login and token generation.
 */
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final AppUserService appUserService;

    /**
     * Constructor to initialize JwtAuthenticationFilter with necessary services.
     *
     * @param authenticationManager the AuthenticationManager to authenticate users.
     * @param appUserService the service to manage user appUsers.
     */
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   AppUserService appUserService) {
        this.authenticationManager = authenticationManager;
        this.appUserService = appUserService;
    }

    /**
     * Attempts to authenticate the user based on the provided credentials in the request.
     *
     * @param request the HTTP request containing the authentication data.
     * @param response the HTTP response.
     * @return Authentication the result of the authentication process.
     * @throws AuthenticationException if authentication fails.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            log.info("[{}] Attempting authentication", LocalDateTime.now());

            ObjectMapper objectMapper = new ObjectMapper();
            UserRequest userRequest = objectMapper.readValue(request.getInputStream(), UserRequest.class);

            String username = userRequest.getUsername();
            String password = userRequest.getPassword();

            log.debug("[{}] Authentication attempt for user: {}", LocalDateTime.now(), username);

            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(username, password);

            return authenticationManager.authenticate(authRequest);

        } catch (IOException e) {
            log.error("[{}] Error reading credentials: {}", LocalDateTime.now(), e.getMessage());
            throw new RuntimeException("Failed to read credentials", e);
        }
    }

    /**
     * This method is called when authentication is successful. It generates JWT tokens and saves the refresh token.
     *
     * @param request the HTTP request.
     * @param response the HTTP response.
     * @param chain the filter chain.
     * @param authResult the result of the authentication process.
     * @throws IOException if an I/O error occurs during token generation.
     * @throws ServletException if an error occurs during the filter chain.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException, ServletException {

        log.info("[{}] Authentication successful for user: {}", LocalDateTime.now(), authResult.getName());

        User user = (User) authResult.getPrincipal();

        try {
            // Retrieve the user profile (Entity, not DTO)
            AppUser appUser = appUserService.findByUsernameOrEmail(user.getUsername(), user.getUsername());

            if (appUser == null) {
                log.error("[{}] AppUser not found for user: {}", LocalDateTime.now(), user.getUsername());
                throw new RemoteException("AppUser not found for user: " + user.getUsername());
            }

            // Check login permissions
            if (!appUser.canLogin()) {
                log.warn("[{}] User cannot login (inactive/deleted): {}", LocalDateTime.now(), appUser.getUsername());
                throw new RemoteException("User: " + appUser.getUsername() + " cannot login");
            }

            // Generate JWT tokens
            Algorithm algorithm = Algorithm.HMAC512(JwtUtil.SECRET);
            List<String> roles = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            Tokens tokens = JwtUtil.generateTokens(user,appUser, roles, algorithm, request);

            log.info("[{}] JWT tokens generated successfully for user: {}", LocalDateTime.now(), user.getUsername());

            // Update login status
            appUser = appUserService.login(appUser.getId());

            if (appUser != null) {
                log.info("[{}] Login status updated for user: {}", LocalDateTime.now(), appUser.getUsername());
            }



            TokenResponse tokenResponse = new TokenResponse(
                    tokens.getAccessToken(),
                    tokens.getRefreshToken()
            );

            log.info("[{}] Authentication completed successfully for user: {}", LocalDateTime.now(), user.getUsername());

            // Send JSON response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            new ObjectMapper().writeValue(response.getOutputStream(), tokenResponse);

        } catch (Exception e) {
            log.error("[{}] Error during successful authentication processing: {}", LocalDateTime.now(), e.getMessage(), e);

            // Error handling with appropriate response
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String errorResponse = """
                {
                    "error": "Authentication processing failed",
                    "message": "%s",
                    "timestamp": "%s"
                }
                """.formatted(e.getMessage(), LocalDateTime.now());

            response.getWriter().write(errorResponse);
        }
    }

    /**
     * This method is called when authentication fails.
     *
     * @param request the HTTP request.
     * @param response the HTTP response.
     * @param failed the authentication exception that caused the failure.
     * @throws IOException if an I/O error occurs.
     * @throws ServletException if an error occurs during the filter chain.
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {

        log.warn("[{}] Authentication failed: {}", LocalDateTime.now(), failed.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String errorResponse = """
            {
                "error": "Authentication failed",
                "message": "%s",
                "timestamp": "%s"
            }
            """.formatted(failed.getMessage(), LocalDateTime.now());

        response.getWriter().write(errorResponse);
    }
}