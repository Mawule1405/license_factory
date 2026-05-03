package com.taurustechnology.backend.securities.jwt;


import com.taurustechnology.backend.dtos.TokenResponse;
import com.taurustechnology.backend.dtos.requests.UserRequest;
import com.taurustechnology.backend.models.AppUser;
import com.taurustechnology.backend.repositories.AppUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor // Utilise Lombok pour injecter les dépendances finales
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository appUserRepository;
    private final JwtUtil jwtUtil; // Injecté proprement
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(@NonNull HttpServletRequest request,
                                                @NonNull HttpServletResponse response)
            throws AuthenticationException {
        try {
            UserRequest userRequest = objectMapper.readValue(request.getInputStream(), UserRequest.class);
            log.info("Tentative de connexion pour : {}", userRequest.getUsername());

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword())
            );
        } catch (IOException e) {
            throw new RuntimeException("Erreur de lecture des identifiants", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException {

        User user = (User) authResult.getPrincipal();
        AppUser appUser = appUserRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé en base"));

        if (!appUser.isActivated() || appUser.getDeletedAt()!= null) {
            this.unsuccessfulAuthentication(request, response,
                    new org.springframework.security.authentication.DisabledException("Compte inactif"));
            return;
        }

        // Mise à jour de l'utilisateur
        appUser.setLoggedIn(true);
        appUserRepository.save(appUser);

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // Génération des tokens
        TokenResponse tokens = jwtUtil.generateTokens(user, appUser, roles, request);

        // 1. CRÉATION DU COOKIE (Refresh Token)
        ResponseCookie resCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .secure(false) // À mettre sur true en production (HTTPS)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();

        // 2. AJOUT DU COOKIE À LA RÉPONSE (Étape manquante dans ton code)
        response.addHeader(HttpHeaders.SET_COOKIE, resCookie.toString());

        // 3. RÉPONSE JSON (On ne renvoie QUE l'accessToken pour la sécurité)
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // On crée une map propre pour ne pas exposer le refreshToken dans le JSON
        Map<String, String> body = new HashMap<>();
        body.put("accessToken", tokens.getAccessToken());

        objectMapper.writeValue(response.getOutputStream(), body);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorBody.put("error", "Échec d'authentification");
        errorBody.put("message", "Identifiants invalides ou compte désactivé");
        errorBody.put("timestamp", LocalDateTime.now().toString());

        objectMapper.writeValue(response.getOutputStream(), errorBody);
    }
}