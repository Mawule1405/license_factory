package com.taurustechnology.backend.services.impl;


import com.taurustechnology.backend.models.AppRole;
import com.taurustechnology.backend.repositories.AppRoleRepository;
import com.taurustechnology.backend.services.AppRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the AppRoleService interface providing CRUD operations
 * for application roles management.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class AppRoleServiceImpl implements AppRoleService {

    private final AppRoleRepository appRoleRepository;


    /**
     * Saves a new application role to the database.
     * Ensures the role ID is null before saving to prevent accidental updates.
     *
     * @param appRole the role entity to be saved
     * @return the saved AppRole entity with generated ID
     * @throws IllegalArgumentException if the appRole parameter is null
     */
    @Override
    public AppRole save(AppRole appRole) {
        if (appRole == null) {
            throw new IllegalArgumentException("AppRole cannot be null");
        }

        appRole.setId(null);
        return appRoleRepository.save(appRole);
    }


    /**
     * Retrieves an application role by its unique identifier.
     *
     * @param id the unique identifier of the role to find
     * @return an Optional containing the found AppRole if exists,
     *         or an empty Optional if not found
     * @throws IllegalArgumentException if the id parameter is null or empty
     */
    @Override
    public Optional<AppRole> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Role ID cannot be null or empty");
        }

        return appRoleRepository.findById(id);
    }

    /**
     * Retrieves all application roles from the database.
     *
     * @return a list of all AppRole entities, empty list if no roles exist
     */
    @Override
    public List<AppRole> findAll() {
        return appRoleRepository.findAll();
    }

    /**
     * Checks if a role with the given name already exists.
     *
     * @param name the role name to check
     * @return true if a role with the name exists, false otherwise
     * @throws IllegalArgumentException if the name parameter is null or empty
     */
    @Override
    public boolean existsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }

        return appRoleRepository.existsByName(name);
    }


    /**
     * Deletes an application role by its unique identifier.
     *
     * @param id the unique identifier of the role to delete
     * @throws IllegalArgumentException if the id parameter is null or empty
     */
    @Override
    public void deleteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Role ID cannot be null or empty");
        }

        appRoleRepository.deleteById(id);
    }
}