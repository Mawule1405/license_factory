package com.taurustechnology.backend.configs.initialization;

import com.taurustechnology.backend.entities.AppRole;
import com.taurustechnology.backend.entities.AppUser;
import com.taurustechnology.backend.repositories.AppRoleRepository;
import com.taurustechnology.backend.repositories.AppUserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Component responsible for initializing the default super administrator user
 * during application startup. Ensures that at least one super admin exists
 * with the appropriate role assignments.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AppUserInitializer {

    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;
    private final AppRoleInitializer appRoleInitializer;
    private final PasswordEncoder passwordEncoder;

    /**
     * Default administrator configuration (can be externalized)
     */
    @Value("${app.admin.username:admin}")
    private String defaultAdminUsername;

    @Value("${app.admin.email:admin@example.com}")
    private String defaultAdminEmail;

    @Value("${app.admin.password:Admin@123}")
    private String defaultAdminPassword;

    private static final String SUPER_ADMIN_ROLE_NAME = "ADMINISTRATEUR";

    /**
     * Creates the default super administrator user if it doesn't exist.
     * Ensures the SUPER-ADMINISTRATEUR role exists and assigns it to the admin user.
     * This method runs automatically after dependency injection is complete.
     */
    @PostConstruct
    public void createSuperAdministrator() {
        try {
            Optional<AppUser> existingSuperAdmin = appUserRepository.findByAppRoles_name(SUPER_ADMIN_ROLE_NAME);

            if (existingSuperAdmin.isPresent()) {
                log.debug("Super administrator user already exists, skipping creation");
                return;
            }

            AppRole superAdminRole = ensureSuperAdminRoleExists();
            createSuperAdminUser(superAdminRole);

        } catch (Exception e) {
            log.error("Failed to initialize super administrator user", e);
        }
    }

    /**
     * Ensures the SUPER-ADMINISTRATEUR role exists in the database.
     * If the role doesn't exist, triggers role initialization.
     *
     * @return the existing or newly created SUPER-ADMINISTRATEUR role
     */
    private AppRole ensureSuperAdminRoleExists() {
        // Vérifier d'abord si le rôle existe
        AppRole appRole = appRoleRepository.findByName(SUPER_ADMIN_ROLE_NAME);

        if (appRole != null) {
            return appRole;
        }

        log.info("Super admin role not found, initializing roles...");
        appRoleInitializer.initializeRoles();

        // Re-vérifier après l'initialisation
        appRole = appRoleRepository.findByName(SUPER_ADMIN_ROLE_NAME);
        if (appRole == null) {
            throw new IllegalStateException(
                    "ADMINISTRATEUR role not found after initialization");
        }

        return appRole;
    }

    /**
     * Creates the super administrator user with the appropriate role assignment.
     *
     * @param superAdminRole the role to assign to the super admin user
     */
    private void createSuperAdminUser(AppRole superAdminRole) {
        // Vérifier d'abord si un utilisateur avec le même username ou email existe déjà
        if (appUserRepository.existsByUsername(defaultAdminUsername)) {
            log.warn("User with username '{}' already exists, skipping super admin creation", defaultAdminUsername);
            return;
        }

        if (defaultAdminEmail != null && appUserRepository.existsByEmail(defaultAdminEmail)) {
            log.warn("User with email '{}' already exists, skipping super admin creation", defaultAdminEmail);
            return;
        }

        AppUser superAdmin = AppUser.builder()
                .username(defaultAdminUsername)
                .email(defaultAdminEmail)
                .passwordHash(passwordEncoder.encode(defaultAdminPassword))
                .activated(true)
                .deleted(false)
                .loggedIn(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Initialiser la liste des rôles et ajouter le rôle
        superAdmin.setAppRoles(java.util.Collections.singletonList(superAdminRole));

        AppUser savedUser = appUserRepository.save(superAdmin);

        log.info("Created default super administrator user: {}", savedUser.getUsername());
        log.warn("Default admin password should be changed after first login for security reasons");

        // Log des détails de l'utilisateur créé (sans le mot de passe)
        log.debug("Super admin user details - ID: {}, Username: {}, Email: {}, Roles: {}",
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getAppRoles() != null ? savedUser.getAppRoles().size() : 0);
    }

    /**
     * Checks if a super administrator user already exists.
     *
     * @return true if a super admin user exists, false otherwise
     */
    public boolean superAdminExists() {
        return appUserRepository.findByAppRoles_name(SUPER_ADMIN_ROLE_NAME).isPresent();
    }
}