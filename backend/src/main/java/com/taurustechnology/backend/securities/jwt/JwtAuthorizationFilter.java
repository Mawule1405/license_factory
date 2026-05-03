package com.taurustechnology.backend.securities.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.taurustechnology.backend.models.AppUser;
import com.taurustechnology.backend.repositories.AppUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final AppUserRepository userRepository;
    private final JwtUtil jwtUtil; // Injection propre du Bean configuré

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(JwtUtil.AUTH_HEADER);

        // 1. Si pas de token, on laisse passer vers la suite (Spring Security bloquera si la route est protégée)
        if (authorizationHeader == null || !authorizationHeader.startsWith(JwtUtil.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);

            return;
        }

        try {
            // 2. Extraction et validation du token

            String token = authorizationHeader.substring(JwtUtil.TOKEN_PREFIX.length());
            Algorithm algorithm = Algorithm.HMAC512(jwtUtil.getSecret());
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = jwtVerifier.verify(token);

            String username = decodedJWT.getSubject();
            List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

            // 3. Vérification de l'état du compte en base (Optionnel mais recommandé pour la sécurité)
            // On le fait ici pour gérer le "locked" ou "deleted" en temps réel
            AppUser appUser = userRepository.findByUsernameOrEmail(username,username)
                    .orElse(null);

            if (appUser == null || appUser.canLogin() ) {
                log.warn("Tentative d'accès avec un compte désactivé/bloqué : {}", username);
                sendErrorResponse(response, "Compte inactif ou supprimé", HttpServletResponse.SC_FORBIDDEN, request.getServletPath());
                return;
            }

            // 4. Authentification dans le contexte Spring
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (Exception e) {

            log.error("Validation du token échouée : {}", e.getMessage());
            sendErrorResponse(response, "Session expirée ou invalide", HttpServletResponse.SC_UNAUTHORIZED, request.getServletPath());
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // On utilise la liste des chemins autorisés définie dans JwtUtil
        return JwtUtil.isPermittedPath(request.getServletPath());
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int status, String path) throws IOException {
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
}