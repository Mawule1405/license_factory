package com.taurustechnology.backend.securities.service;


import com.taurustechnology.backend.repositories.AppUserRepository;
import com.taurustechnology.backend.securities.jwt.JwtAuthenticationFilter;
import com.taurustechnology.backend.securities.jwt.JwtAuthorizationFilter;
import com.taurustechnology.backend.securities.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
@Slf4j
public class WebSecurity {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final AppUserRepository appUserRepository;
    private final JwtUtil jwtUtil; // Ajout nécessaire pour les filtres

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        log.info("[{}] Configuring security filter chain", LocalDateTime.now());

        // Initialisation des filtres avec les dépendances requises
        AuthenticationManager authManager = authenticationManager();

        JwtAuthenticationFilter authFilter = new JwtAuthenticationFilter(authManager, appUserRepository, jwtUtil);
        authFilter.setFilterProcessesUrl("/login");

        JwtAuthorizationFilter authorizationFilter = new JwtAuthorizationFilter(appUserRepository,jwtUtil);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/api/auth/refresh/**",
                                "/api/users/register",
                                "/api/tickets/take",
                                "/api/public/**", // Pour les scans QR code
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/error"
                        ).permitAll()

                        // Sécurisation globale
                        .anyRequest().authenticated()
                )

                // Positionnement des filtres
                .addFilter(authFilter)
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) ->
                                sendError(response, "Authentification requise", 401, request.getServletPath()))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                sendError(response, "Droits insuffisants", 403, request.getServletPath()))
                );

        return http.build();
    }

    /**
     * Méthode utilitaire pour uniformiser les erreurs de sécurité avec ton ErrorResponse
     */
    private void sendError(HttpServletResponse response, String message, int status, String path) throws IOException, IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String errorResponse = """
                {
                    "status": %d,
                    "message": "%s",
                    "timestamp": "%s",
                    "path": "%s"
                }
                """.formatted(status, message, LocalDateTime.now(), path);

        response.getWriter().write(errorResponse);
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}