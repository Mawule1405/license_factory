package com.taurustechnology.backend.services.impl;

import com.taurustechnology.backend.entities.AppRole;
import com.taurustechnology.backend.entities.AppUser;
import com.taurustechnology.backend.services.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Custom implementation of UserDetailsService to load user-specific data
 * for Spring Security authentication and authorization.
 * This service bridges the application's user entities with Spring Security's requirements.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserDetailService implements UserDetailsService {

    private final AppUserService appUserService;

    /**
     * Loads the user by username or email and builds a UserDetails object
     * with granted authorities (roles) for Spring Security authentication.
     *
     * @param username the username or email of the user to authenticate
     * @return a fully populated UserDetails instance
     * @throws UsernameNotFoundException if the user is not found, inactive, or deleted
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user by identifier: {}", username);

        try {
            // Retrieve user profile by username or email
            AppUser appUser = appUserService.findByUsernameOrEmail(username, username);

            if (appUser == null) {
                log.warn("User not found with identifier: {}", username);
                throw new UsernameNotFoundException("User not found: " + username);
            }

            // Verify if user can login (active and not deleted)
            if (!canUserLogin(appUser)) {
                log.warn("User account cannot login: {} (activated: {}, deleted: {})",
                        appUser.getUsername(), appUser.isActivated(), appUser.isDeleted());
                throw new UsernameNotFoundException("User account is disabled or deleted: " + username);
            }

            log.debug("User found: {}. Fetching roles...", appUser.getUsername());

            // Extract role names from user entity
            List<String> roleNames = extractRoleNames(appUser);

            // Create Spring Security authorities from roles
            Collection<GrantedAuthority> authorities = createAuthorities(roleNames);

            log.info("User {} loaded successfully with {} roles", appUser.getUsername(), roleNames.size());

            // Create Spring Security UserDetails object
            return createUserDetails(appUser, authorities);

        } catch (UsernameNotFoundException e) {
            log.error("Authentication failed for user: {} - {}", username, e.getMessage());
            throw e; // Re-throw for Spring Security

        } catch (Exception e) {
            log.error("Unexpected error loading user {}: {}", username, e.getMessage(), e);
            throw new UsernameNotFoundException("Error loading user: " + username, e);
        }
    }

    /**
     * Checks if a user account is eligible for login.
     * An account can login if it's activated and not deleted.
     *
     * @param appUser the user entity to check
     * @return true if the user can login, false otherwise
     */
    private boolean canUserLogin(AppUser appUser) {
        return appUser.isActivated() && !appUser.isDeleted();
    }

    /**
     * Extracts role names from the user entity.
     *
     * @param appUser the user entity containing roles
     * @return list of role names assigned to the user
     */
    private List<String> extractRoleNames(AppUser appUser) {
        List<String> roleNames = new ArrayList<>();

        if (appUser.getAppRoles() != null && !appUser.getAppRoles().isEmpty()) {
            for (AppRole role : appUser.getAppRoles()) {
                if (role != null && role.getName() != null) {
                    roleNames.add(role.getName());
                    log.debug("Assigning role: {} to user: {}", role.getName(), appUser.getUsername());
                }
            }
        } else {
            log.warn("User {} has no roles assigned", appUser.getUsername());
            // Add default role if no roles are assigned
            roleNames.add("USER");
        }

        return roleNames;
    }

    /**
     * Creates Spring Security authorities from role names.
     * Adds both the raw role name and the ROLE_ prefixed version for compatibility.
     *
     * @param roleNames the list of role names to convert
     * @return collection of GrantedAuthority objects
     */
    private Collection<GrantedAuthority> createAuthorities(List<String> roleNames) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        for (String roleName : roleNames) {
            // Add the role as-is (for custom authority checks)
            //authorities.add(new SimpleGrantedAuthority(roleName));

            // Add with ROLE_ prefix for Spring Security's hasRole() method
            if (!roleName.startsWith("ROLE_")) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
            }
        }

        log.debug("Created {} authorities for user", authorities.size());
        return authorities;
    }

    /**
     * Creates a Spring Security UserDetails object from application user entity.
     *
     * @param appUser the application user entity
     * @param authorities the collection of granted authorities
     * @return a UserDetails instance for Spring Security
     */
    private UserDetails createUserDetails(AppUser appUser, Collection<GrantedAuthority> authorities) {
        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPasswordHash())
                .authorities(authorities)
                .accountExpired(false) // You might want to implement account expiration logic
                .accountLocked(false)  // You might want to implement account locking logic
                .credentialsExpired(false) // You might want to implement credentials expiration
                .disabled(!appUser.isActivated() || appUser.isDeleted()) // Disabled if not activated or deleted
                .build();
    }

    /**
     * Optional: Method to reload user details for security context refresh
     *
     * @param username the username to reload
     * @return updated UserDetails
     */
    public UserDetails reloadUser(String username) {
        log.debug("Reloading user details for: {}", username);
        return loadUserByUsername(username);
    }
}