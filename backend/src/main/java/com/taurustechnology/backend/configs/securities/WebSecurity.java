package com.taurustechnology.backend.configs.securities;


import com.taurustechnology.backend.services.AppUserService;
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

import java.time.LocalDateTime;

/**
 * Configuration de la sécurité Spring Security pour l'application.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
@Slf4j
public class WebSecurity {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final AppUserService appUserService;


    /**
     * Configure la chaîne de filtres de sécurité.
     *
     * @param http l'objet HttpSecurity à configurer
     * @return la SecurityFilterChain configurée
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("[{}] Configuring security filter chain", LocalDateTime.now());

        http
                // Désactivation CSRF pour les APIs REST
                .csrf(AbstractHttpConfigurer::disable)

                // Session stateless pour JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configuration des headers de sécurité
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                        .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                                .maxAgeInSeconds(31536000)
                                .includeSubDomains(true))
                )

                // Configuration des autorisations
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/logout",
                                "/auth/refresh-token"
                        ).permitAll()
                        .anyRequest().permitAll()
                )

                // Ajout des filtres JWT
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), appUserService))
                .addFilterBefore(new JwtAuthorizationFilter(appUserService), UsernamePasswordAuthenticationFilter.class)

                // Gestion des exceptions d'authentification
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.warn("[{}] Authentication failed for path: {}", LocalDateTime.now(), request.getServletPath());
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            String errorResponse = """
                        {
                            "error": "Authentication required",
                            "message": "%s",
                            "status": 401,
                            "timestamp": "%s",
                            "path": "%s"
                        }
                        """.formatted(authException.getMessage(), LocalDateTime.now(), request.getServletPath());
                            response.getWriter().write(errorResponse);
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.warn("[{}] Access denied for path: {}", LocalDateTime.now(), request.getServletPath());
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            String errorResponse = """
                        {
                            "error": "Access denied",
                            "message": "%s",
                            "status": 403,
                            "timestamp": "%s",
                            "path": "%s"
                        }
                        """.formatted(accessDeniedException.getMessage(), LocalDateTime.now(), request.getServletPath());
                            response.getWriter().write(errorResponse);
                        })
                );

        log.info("[{}] Security filter chain configured successfully", LocalDateTime.now());
        return http.build();
    }

    /**
     * Expose le bean AuthenticationManager.
     *
     * @return le bean AuthenticationManager
     * @throws Exception en cas d'erreur d'initialisation
     */
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        log.info("[{}] Creating AuthenticationManager bean", LocalDateTime.now());
        return authenticationConfiguration.getAuthenticationManager();
    }

}