package com.taurustechnology.backend.controllers;


import com.taurustechnology.backend.dtos.requests.AppRoleRequest;
import com.taurustechnology.backend.dtos.responses.AppRoleResponse;
import com.taurustechnology.backend.models.AppRole;
import com.taurustechnology.backend.mappers.AppRoleMapper;
import com.taurustechnology.backend.services.AppRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller for managing {@link AppRole} resources.
 * Provides endpoints for role creation, retrieval, and listing.
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class AppRoleController {

    private final AppRoleService appRoleService;
    private final AppRoleMapper appRoleMapper;

    /**
     * Retrieve all application roles.
     *
     * @return a list of all roles as DTOs
     */
    @GetMapping("/all")
    public ResponseEntity<List<AppRoleResponse>> findAllAppRoles() {
        List<AppRole> appRoles = appRoleService.findAll();
        return ResponseEntity.ok(appRoleMapper.toDTO(appRoles));
    }

    /**
     * Find a role by its ID.
     *
     * @param id the role ID
     * @return the found role as DTO, or 404 if not found
     */
    @GetMapping("/find/{id}")
    public ResponseEntity<AppRoleResponse> findAppRoleById(@PathVariable String id) {
        Optional<AppRole> appRole = appRoleService.findById(id);
        return appRole
                .map(role -> ResponseEntity.ok(appRoleMapper.toDTO(role)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new application role.
     *
     * @param appRoleRequest the role data transfer object
     * @return the created role as DTO
     */
    @PostMapping("/create")
    public ResponseEntity<AppRoleResponse> createAppRole(@RequestBody @Valid AppRoleRequest appRoleRequest) {
        AppRole appRole = appRoleMapper.toEntity(appRoleRequest);
        AppRole savedRole = appRoleService.save(appRole);
        return ResponseEntity.ok(appRoleMapper.toDTO(savedRole));
    }
}