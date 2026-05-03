package com.taurustechnology.backend.controllers;


import com.taurustechnology.backend.dtos.*;
import com.taurustechnology.backend.dtos.requests.AppRoleRequest;
import com.taurustechnology.backend.dtos.requests.AppUserRequest;
import com.taurustechnology.backend.dtos.requests.AppUserPasswordChangingRequest;
import com.taurustechnology.backend.dtos.requests.AppUserUpdateRequest;
import com.taurustechnology.backend.dtos.responses.AppUserResponse;
import com.taurustechnology.backend.dtos.responses.Pagination;
import com.taurustechnology.backend.models.AppUser;
import com.taurustechnology.backend.mappers.AppRoleMapper;
import com.taurustechnology.backend.mappers.AppUserMapper;
import com.taurustechnology.backend.services.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * Controller for managing {@link AppUser} resources.
 * Provides endpoints for user creation, retrieval, update, and logout.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;
    private final AppUserMapper appUserMapper;
    private final AppRoleMapper appRoleMapper;

    /**
     * Create a new application user.
     *
     * @param appUserRequest       the user data transfer object
     * @return the created user as DTO
     */
    @PostMapping("/create") // Route simplifiée
    public ResponseEntity<AppUserResponse> createAppUser(@RequestBody AppUserRequest appUserRequest,
                                                         Principal principal) {
        // Principal contient le username extrait du JWT
        String adminUsername = principal.getName();

        AppUser appUser = appUserMapper.toEntity(appUserRequest);
        // On passe le username ou on cherche l'ID en service
        appUser = appUserService.create(appUser, adminUsername);

        return ResponseEntity.ok(appUserMapper.toDTO(appUser));
    }



    @GetMapping("/search")
    public ResponseEntity<Pagination<AppUserResponse>> search(
                @RequestParam(name = "keyword", defaultValue = "") String keyword,
                @RequestParam(name = "page", defaultValue = "0") int page,
                @RequestParam(name = "size", defaultValue = "5") int size) {
            Page<AppUserResponse> responses = appUserService.searchUsers(keyword, page, size);

            return ResponseEntity.ok(Pagination.of(responses));
        }


    /**
     * Find a user by their ID.
     *
     * @param appUserId the user ID
     * @return the found user as DTO
     */
    @GetMapping("/find/{userId}")
    public ResponseEntity<AppUserResponse> findById(@PathVariable("userId") String appUserId) {
        AppUser appUser = appUserService.findById(appUserId);
        return ResponseEntity.ok(appUserMapper.toDTO(appUser));
    }

    /**
     * Find a users with have the administrator role
     *
     * @return the list of administrator
     */
    @GetMapping("/find/all-admin")
    public ResponseEntity<Pagination<AppUserResponse>> findAllAdministrators(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Page<AppUser> appUsers = appUserService.findAdministrators(page, size);
        Page<AppUserResponse> appUserResponses = appUsers.map(appUserMapper::toDTO);
        return ResponseEntity.ok(Pagination.of(appUserResponses));
    }


    /**
     * Update a user's information.
     *
     * @param updateUser the new user data
     * @param appUserId  the ID of the user to update
     * @return the updated user as DTO
     */
    @PutMapping("/update/{userId}")
    public ResponseEntity<AppUserResponse> updateAppUser(@RequestBody @Valid AppUserUpdateRequest updateUser,
                                                        @PathVariable("userId") String appUserId) {
        AppUser appUser = AppUser.builder()

                .username(updateUser.getUsername())
                .email(updateUser.getEmail())
                .fullName(updateUser.getFullName())
                .build();
        appUser = appUserService.update(appUser, appUserId);
        return ResponseEntity.ok(appUserMapper.toDTO(appUser));
    }

    /**
     * Log out a user.
     *
     * @param appUserId the user ID
     * @return the user as DTO after logout
     */
    @PatchMapping("/logout/{userId}")
    public ResponseEntity<AppUserResponse> logout(@PathVariable("userId") String appUserId) {
        AppUser appUser = appUserService.logout(appUserId);
        return ResponseEntity.ok(appUserMapper.toDTO(appUser));
    }

    @GetMapping("/check-if/{username}/exists")
    public ResponseEntity<Boolean> checkIfExists(@PathVariable("username") String username) {
        return ResponseEntity.ok(appUserService.checkIfUsernameExists(username));
    }

    @GetMapping("/find-by-username/{username}")
    public ResponseEntity<AppUserResponse> findByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(appUserMapper.toDTO(appUserService.findByUsernameOrEmail(username,username)));
    }

    @PatchMapping("/change-password/{userId}")
    public ResponseEntity<AppUserResponse> changePassword(@PathVariable String userId,
                                                         @RequestBody AppUserPasswordChangingRequest data){

        return ResponseEntity.ok(appUserMapper.toDTO(appUserService.changePassword(userId, data.getOldPassword(),data.getNewPassword())));
    }

    @PatchMapping("/{initializerId}/initialize-password/{userId}")
    public ResponseEntity<AppUserResponse> initialize(@PathVariable String initializerId, @PathVariable String userId) {
        return ResponseEntity.ok(appUserMapper.toDTO(appUserService.initialize(initializerId, userId, "123456789")));
    }

    @PatchMapping("/change-app-roles/{id}")
    public ResponseEntity<AppUserResponse> changeAppRoles(@PathVariable String id, @RequestBody List<AppRoleRequest> newRoles) {
        return ResponseEntity.ok(appUserMapper.toDTO(appUserService.changeAppRoles(id, appRoleMapper.toEntity(newRoles))));
    }

    @PatchMapping("/change-credentials/{id}")
    public ResponseEntity<AppUserResponse> changeCredentials(@PathVariable String id, @RequestBody CredentialDTO credentialDTO) {
        return ResponseEntity.ok(appUserMapper.toDTO(appUserService.changeCredential(id, credentialDTO.getUsername(), credentialDTO.getEmail())));
    }

    @GetMapping("/count/all")
    public ResponseEntity<Long> countAll() {
        return ResponseEntity.ok(appUserService.countAll());
    }

    @GetMapping("/find/all")
    public ResponseEntity<Pagination<AppUserResponse>> findAllAppUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                @RequestParam(value = "size", defaultValue = "20") int size) {

        Page<AppUser> appUsers = appUserService.findAllAppUser(page, size);
        Page<AppUserResponse> appUserResponses = appUsers.map(appUserMapper::toDTO);
        return ResponseEntity.ok(Pagination.of(appUserResponses));
    }
}