package com.taurustechnology.backend.configs.initialization;


import com.taurustechnology.backend.entities.AppRole;
import com.taurustechnology.backend.repositories.AppRoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Component responsible for initializing application roles in the database
 * during application startup. Ensures that essential roles are created
 * if they don't already exist.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AppRoleInitializer {

    private final AppRoleRepository appRoleRepository;


    /**
     * Role names and descriptions used for initialization
     */
    private static final String ADMIN_ROLE = "ADMINISTRATEUR";
    private static final String USER_ROLE = "USER";

    private static final String ADMIN_DESC = "System administrator with full access and management rights "
            + "for all modules";
    private static final String USER_DESC = "Internal users with access to daily work tools "
            + "and basic functionalities";




    /**
     * Initializes the database with predefined roles identified during system design.
     * Checks if the role table is already initialized before proceeding.
     * This method runs automatically after dependency injection is complete.
     */
    @PostConstruct
    public void initializeRoles() {
        boolean isInitialized = appRoleRepository.count() > 0;

        if (!isInitialized) {
            log.info("Starting application role initialization...");

            createRoleIfNotExists(ADMIN_ROLE, ADMIN_DESC);
            createRoleIfNotExists(USER_ROLE, USER_DESC);


            log.info("Application role initialization completed successfully");
        } else {
            log.debug("Role table already initialized, skipping role creation");
        }
    }



    /**
     * Creates a new application role if it doesn't already exist in the database.
     *
     * @param name the name of the role to create
     * @param description the description of the role
     */
    private void createRoleIfNotExists(String name, String description) {
        if (!appRoleRepository.existsByName(name)) {
            AppRole role = AppRole.builder()
                    .name(name)
                    .description(description)
                    .build();

            AppRole savedRole = appRoleRepository.save(role);
            log.info("Created new application role: {}", savedRole.getName());
        } else {
            log.debug("Role '{}' already exists, skipping creation", name);
        }
    }
}