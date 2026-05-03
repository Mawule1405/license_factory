package com.taurustechnology.backend.securities.service;

import com.taurustechnology.backend.models.AppUser;
import com.taurustechnology.backend.services.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserDetailService implements UserDetailsService {

    private final AppUserService appUserService;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        log.info("🔍 Authentication attempt for identifier: {}", identifier);

        // 1. Recherche de l'utilisateur
        AppUser appUser = appUserService.findByUsernameOrEmail(identifier, identifier);

        if (appUser == null) {
            log.warn("❌ User not found: {}", identifier);
            // On reste vague sur l'erreur pour la sécurité (énumération de comptes)
            throw new UsernameNotFoundException("Invalid credentials");
        }

        // 2. Vérification du statut du compte
        if (!appUser.isActivated() || appUser.getDeletedAt()!=null) {
            log.warn("🚫 Account disabled or deleted: {} (Active: {}, Deleted: {})",
                    appUser.getUsername(), appUser.isActivated(), appUser.getDeletedAt());
            throw new UsernameNotFoundException("Account is inactive or has been removed");
        }

        // 3. Conversion des rôles en Authorities (Format ROLE_XXX)
        // On s'assure que chaque rôle a le préfixe ROLE_ une seule fois
        List<SimpleGrantedAuthority> authorities = appUser.getAppRoles().stream()
                .map(role -> {
                    String roleName = role.getName().toUpperCase();
                    if (!roleName.startsWith("ROLE_")) {
                        roleName = "ROLE_" + roleName;
                    }
                    return new SimpleGrantedAuthority(roleName);
                })
                .collect(Collectors.toList());

        // Sécurité : si aucun rôle n'est trouvé, on refuse l'accès
        if (authorities.isEmpty()) {
            log.error("⚠️ User {} has no assigned roles. Access denied.", identifier);
            throw new UsernameNotFoundException("User has no permissions");
        }

        log.info("✅ User {} loaded with authorities: {}", appUser.getUsername(), authorities);

        // 4. Construction de l'objet UserDetails de Spring Security
        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword()) // Doit impérativement être hashé en BCrypt en DB
                .authorities(authorities)
                .disabled(!appUser.isActivated())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }
}