package com.taurustechnology.backend.configs.securities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.taurustechnology.backend.entities.AppUser;
import com.taurustechnology.backend.services.AppUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Filter for JWT authorization, processing JWT tokens in requests and authenticating users.
 */
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final AppUserService appUserService;

    public JwtAuthorizationFilter(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    /**
     * Processes incoming requests, validates JWT token, and sets authentication context if token is valid.
     *
     * @param request the HTTP request containing the JWT token
     * @param response the HTTP response to send back to the client
     * @param filterChain the filter chain to continue processing
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        String authorizationHeader = request.getHeader(JwtUtil.AUTH_HEADER);
        String requestPath = request.getServletPath();

        log.debug("[{}] Processing request to: {}", LocalDateTime.now(), requestPath);

        if (JwtUtil.isPermittedPath(requestPath)) {
            log.debug("[{}] Request to permitted path: {}", LocalDateTime.now(), requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        if (authorizationHeader != null && authorizationHeader.startsWith(JwtUtil.TOKEN_PREFIX)) {
            try {
                log.debug("[{}] JWT token found, attempting to validate", LocalDateTime.now());
                String token = authorizationHeader.substring(JwtUtil.TOKEN_PREFIX.length());

                Algorithm algorithm = Algorithm.HMAC512(JwtUtil.SECRET);
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(token);

                String username = decodedJWT.getSubject();
                List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

                log.debug("[{}] Token decoded for user: {} with roles: {}", LocalDateTime.now(), username, roles);

                AppUser appUser = appUserService.findByUsernameOrEmail(username, username);

                if (appUser == null) {
                    sendErrorResponse(request, response, "User profile not found", HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                if (!appUser.isLoggedIn()) {
                    sendErrorResponse(request, response, "User has not logged in", HttpServletResponse.SC_FORBIDDEN);
                    return;
                }


                if (!appUser.canLogin()) {
                    sendErrorResponse(request, response, "User cannot log in. The account is deleted or deactivated", HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                Collection<GrantedAuthority> authorities = new ArrayList<>();
                if (roles != null) {
                    roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                filterChain.doFilter(request, response);

            } catch (Exception e) {

                sendErrorResponse(request, response, "Invalid or expired token", HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            sendErrorResponse(request, response, "Authorization token is required", HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    /**
     * Sends a structured JSON error response.
     *
     * @param request the HTTP request (for path reporting)
     * @param response the HTTP response
     * @param message the error message
     * @param status the HTTP status code
     * @throws IOException if an I/O error occurs
     */
    private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response, String message, int status) throws IOException {
        log.debug("[{}] Sending error response: {} with status: {}", LocalDateTime.now(), message, status);

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String errorResponse = """
            {
                "error": true,
                "message": "%s",
                "status": %d,
                "timestamp": "%s",
                "path": "%s"
            }
            """.formatted(
                message,
                status,
                LocalDateTime.now(),
                request.getServletPath()
        );

        response.getWriter().write(errorResponse);
    }

    /*
     * Determines if the filter should not be applied to the given request.
     * Override to exclude certain paths or request types.
     *
     * @param request the HTTP request
     * @return true if the filter should be skipped, false otherwise
     *
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        boolean shouldSkip = JwtUtil.isPermittedPath(path);

        if (shouldSkip) {
            log.debug("[{}] Skipping JWT filter for permitted path: {}", LocalDateTime.now(), path);
        }

        return shouldSkip;
    }*/
}