package com.taurustechnology.backend.services.impl;


import com.taurustechnology.backend.entities.AppRole;
import com.taurustechnology.backend.entities.AppUser;
import com.taurustechnology.backend.repositories.AppUserRepository;
import com.taurustechnology.backend.services.AppUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for AppUser entity operations.
 * Provides CRUD operations and business logic for user management.
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new application user with the specified creator user ID.
     * Validates input parameters and sets appropriate default values.
     *
     * @param appUser the user entity to create
     * @param createdByAppUserId the ID of the user creating this account
     * @return the created AppUser entity, or null if creator user doesn't exist
     * @throws IllegalArgumentException if appUser parameter is null
     */
    @Override
    public AppUser create(AppUser appUser, String createdByAppUserId) {
        if (appUser == null) {
            throw new IllegalArgumentException("AppUser cannot be null");
        }

        if (createdByAppUserId == null || createdByAppUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("Creator user ID cannot be null or empty");
        }

        Optional<AppUser> creatorUser = appUserRepository.findById(createdByAppUserId);
        if (creatorUser.isEmpty()) {
            log.warn("Creator user with ID {} not found, cannot create new user", createdByAppUserId);
            return null;
        }

        // Check if username or email already exists
        if (appUserRepository.existsByUsername(appUser.getUsername())) {
            log.warn("Username {} already exists", appUser.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        if (appUser.getEmail() != null && appUserRepository.existsByEmail(appUser.getEmail())) {
            log.warn("Email {} already exists", appUser.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        // Set user properties
        appUser.setPasswordHash(passwordEncoder.encode(appUser.getPasswordHash()));
        appUser.setId(null);
        appUser.setActivated(true);
        appUser.setDeleted(false);
        appUser.setLoggedIn(false);
        appUser.setCreatedAt(LocalDateTime.now());
        appUser.setUpdatedAt(LocalDateTime.now());

        AppUser newAppUser = appUserRepository.save(appUser);
        log.info("User {} created new user: {} with ID: {}",
                createdByAppUserId, newAppUser.getUsername(), newAppUser.getId());

        return newAppUser;
    }

    /**
     * Finds a user by username or email address.
     *
     * @param username the username to search for
     * @param email the email address to search for
     * @return the found AppUser entity, or null if not found
     * @throws IllegalArgumentException if both username and email are null or empty
     */
    @Override
    public AppUser findByUsernameOrEmail(String username, String email) {
        if ((username == null || username.trim().isEmpty()) &&
                (email == null || email.trim().isEmpty())) {
            throw new IllegalArgumentException("Either username or email must be provided");
        }

        Optional<AppUser> user;

        if (username != null && !username.trim().isEmpty()) {
            user = appUserRepository.findByUsername(username);
            if (user.isPresent()) {
                return user.get();
            }
        }

        if (email != null && !email.trim().isEmpty()) {
            user = appUserRepository.findByEmail(email);
            if (user.isPresent()) {
                return user.get();
            }
        }

        log.debug("User not found with username: {} or email: {}", username, email);
        return null;
    }

    /**
     * Finds a user by their unique identifier.
     *
     * @param id the user ID to search for
     * @return the found AppUser entity, or null if not found
     * @throws IllegalArgumentException if id parameter is null
     */
    @Override
    public AppUser findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        return appUserRepository.findById(id).orElse(null);
    }

    /**
     * Updates an existing user with the specified updater user ID.
     *
     * @param appUser the user entity with updated information
     * @param updatedByAppUserId the ID of the user performing the update
     * @return the updated AppUser entity, or null if user doesn't exist
     * @throws IllegalArgumentException if appUser parameter is null or has null ID
     */
    @Override
    public AppUser update(AppUser appUser, String updatedByAppUserId) {
        if (appUser == null) {
            throw new IllegalArgumentException("AppUser cannot be null");
        }

        if (appUser.getId() == null || appUser.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null for update operation");
        }

        if (updatedByAppUserId == null || updatedByAppUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("Updater user ID cannot be null or empty");
        }

        Optional<AppUser> existingUser = appUserRepository.findById(appUser.getId());
        if (existingUser.isEmpty()) {
            log.warn("User with ID {} not found for update", appUser.getId());
            return null;
        }

        Optional<AppUser> updaterUser = appUserRepository.findById(updatedByAppUserId);
        if (updaterUser.isEmpty()) {
            log.warn("Updater user with ID {} not found", updatedByAppUserId);
            return null;
        }

        AppUser userToUpdate = existingUser.get();

        // Update fields that are allowed to be modified
        if (appUser.getUsername() != null && !appUser.getUsername().equals(userToUpdate.getUsername())) {
            if (appUserRepository.existsByUsername(appUser.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
            userToUpdate.setUsername(appUser.getUsername());
        }

        if (appUser.getEmail() != null && !appUser.getEmail().equals(userToUpdate.getEmail())) {
            if (appUserRepository.existsByEmail(appUser.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            userToUpdate.setEmail(appUser.getEmail());
        }

        if (appUser.getPasswordHash() != null) {
            userToUpdate.setPasswordHash(passwordEncoder.encode(appUser.getPasswordHash()));
        }

        userToUpdate.setActivated(appUser.isActivated());
        userToUpdate.setUpdatedAt(LocalDateTime.now());

        AppUser updatedUser = appUserRepository.save(userToUpdate);
        log.info("User {} updated user: {}", updatedByAppUserId, updatedUser.getId());

        return updatedUser;
    }


    /**
     * Login an existing user account with the specified user ID.
     *
     * @param appUserId the AppUser ID
     * @return the updated AppUser entity
     * @throws EntityNotFoundException if the user is not found
     */
    @Override
    @Transactional
    public AppUser login(String appUserId) {
        AppUser appUser = appUserRepository.findById(appUserId)
                .orElseThrow(() -> {
                    log.warn("User {} not found", appUserId);
                    return new EntityNotFoundException("User not found with id: " + appUserId);
                });

        appUser.setLoggedIn(true);
        appUser.setUpdatedAt(LocalDateTime.now());
        System.out.println("===========AVANT");
        System.out.println(appUser);
        appUser = appUserRepository.save(appUser);

        System.out.println("===========APRES");
        System.out.println(appUser);

        log.info("User {} logged in", appUser.getId());
        return appUser;
    }


    /**
     * Logout an existing user account with the specified user ID.
     *
     * @param appUserId the AppUser ID
     * @return the updated AppUser entity
     * @throws EntityNotFoundException if the user is not found
     */
    @Override
    @Transactional
    public AppUser logout(String appUserId) {
        AppUser appUser = appUserRepository.findById(appUserId)
                .orElseThrow(() -> {
                    log.warn("User {} not found", appUserId);
                    return new EntityNotFoundException("User not found with id: " + appUserId);
                });

        appUser.setLoggedIn(false);
        appUser.setUpdatedAt(LocalDateTime.now());
        AppUser updatedUser = appUserRepository.save(appUser);

        log.info("User {} logged out", updatedUser.getId());
        return updatedUser;
    }

    /**
     * Checks if a username already exists in the database.
     *
     * @param username the username to check
     * @return true if the username exists, false otherwise
     * @throws IllegalArgumentException if username parameter is null or empty
     */
    @Override
    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        return appUserRepository.existsByUsername(username);
    }

    /**
     * Checks if an email already exists in the database.
     *
     * @param email the email to check
     * @return true if the email exists, false otherwise
     * @throws IllegalArgumentException if email parameter is null or empty
     */
    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return appUserRepository.existsByEmail(email);
    }

    /**
     * Deletes a user by their unique identifier (soft delete).
     *
     * @param id the user ID to delete
     * @param deletedByAppUserId the ID of the user performing the deletion
     * @return true if deletion was successful, false otherwise
     * @throws IllegalArgumentException if id parameter is null or empty
     */
    @Override
    public boolean deleteById(String id, String deletedByAppUserId) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        Optional<AppUser> user = appUserRepository.findById(id);
        if (user.isEmpty()) {
            log.warn("User with ID {} not found for deletion", id);
            return false;
        }

        AppUser userToDelete = user.get();
        userToDelete.setDeleted(true);
        userToDelete.setUpdatedAt(LocalDateTime.now());

        appUserRepository.save(userToDelete);
        log.info("User {} soft-deleted user: {}", deletedByAppUserId, id);

        return true;
    }

    /**
     * Find all the administrator account
     * @return List of administrators
     */
    @Override
    public List<AppUser> findAdministrators(){
        return this.appUserRepository.findByRoleName("ADMINISTRATEUR");
    }

    /**
     * * Check if the username exists
     * @param username : checking username
     * @return True or False
     */
    @Override
    public Boolean checkIfUsernameExists(String username){
        AppUser appUser =  appUserRepository.findByUsername(username).orElse(null);
        return appUser != null;
    }

    @Override
    public AppUser changePassword(String id, String newPassword){
        AppUser appUser = appUserRepository.findById(id).orElse(null);

        if (appUser == null) {
            log.warn("User {} not found", id);
            throw new EntityNotFoundException("User not found with id: " + id);
        }

        appUser.setPasswordHash(passwordEncoder.encode(newPassword));
        appUser.setLoggedIn(false);
        appUser.setUpdatedAt(LocalDateTime.now());
        appUser = appUserRepository.save(appUser);

        log.info("User {} changed password", id);
        return appUser;
    }

    @Override
    public AppUser changeCredential(String id, String username, String email){
        AppUser appUser = appUserRepository.findById(id).orElse(null);

        if (appUser == null) {
            log.warn("User {} not found", id);
            throw new EntityNotFoundException("User not found with id: " + id);
        }

        appUser.setUsername(username);
        appUser.setEmail(email);
        appUser.setLoggedIn(false);
        appUser.setUpdatedAt(LocalDateTime.now());
        appUser = appUserRepository.save(appUser);

        log.info("User {} changed credential", id);
        return appUser;
    }

    @Override
    public long countAll(){
        return appUserRepository.count();
    }

    @Override
    public AppUser changeAppRoles(String id, List<AppRole> newRoles){
        AppUser user = appUserRepository.findById(id).orElse(null);
        if(user == null) {
            log.warn("User {} not found", id);
            throw new EntityNotFoundException("User not found with id: " + id);
        }

        user.setAppRoles(newRoles);
        System.out.println(user);
        user = appUserRepository.save(user);

        log.info("User {} changed roles", id);
        return user;
    }

    @Override
    public List<AppUser> findAllAppUser(){
        return appUserRepository.findAll();
    }
}