package com.taurustechnology.backend.controllers;


import com.taurustechnology.backend.dtos.AppRoleDTO;
import com.taurustechnology.backend.dtos.AppUserDTO;
import com.taurustechnology.backend.dtos.CredentialDTO;
import com.taurustechnology.backend.entities.AppUser;
import com.taurustechnology.backend.mappers.AppRoleMapper;
import com.taurustechnology.backend.mappers.AppUserMapper;
import com.taurustechnology.backend.services.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * @param appUserDTO       the user data transfer object
     * @param createdAppUserId the ID of the user creating the new user
     * @return the created user as DTO
     */
    @PostMapping("/create/{userId}")
    public ResponseEntity<AppUserDTO> createAppUser(@RequestBody AppUserDTO appUserDTO,
                                                    @PathVariable("userId") String createdAppUserId) {
        AppUser appUser = appUserMapper.toEntity(appUserDTO);
        appUser = appUserService.create(appUser, createdAppUserId);
        return ResponseEntity.ok(appUserMapper.toDTO(appUser));
    }



    @GetMapping("/search")
    public ResponseEntity<Page<AppUserDTO>> search(
                @RequestParam(name = "keyword", defaultValue = "") String keyword,
                @RequestParam(name = "page", defaultValue = "0") int page,
                @RequestParam(name = "size", defaultValue = "5") int size) {

            return ResponseEntity.ok(appUserService.searchUsers(keyword, page, size).map(appUserMapper::toDTO));
        }


    /**
     * Find a user by their ID.
     *
     * @param appUserId the user ID
     * @return the found user as DTO
     */
    @GetMapping("/find/{userId}")
    public ResponseEntity<AppUserDTO> findById(@PathVariable("userId") String appUserId) {
        AppUser appUser = appUserService.findById(appUserId);
        return ResponseEntity.ok(appUserMapper.toDTO(appUser));
    }

    /**
     * Find a users with have the administrator role
     *
     * @return the list of administrator
     */
    @GetMapping("/find/all-admin")
    public ResponseEntity<List<AppUserDTO>> findAllAdministrators() {
        List<AppUser> appUsers = appUserService.findAdministrators();
        return ResponseEntity.ok(appUserMapper.toDTO(appUsers));
    }


    /**
     * Update a user's information.
     *
     * @param appUserDTO the new user data
     * @param appUserId  the ID of the user to update
     * @return the updated user as DTO
     */
    @PutMapping("/update/{userId}")
    public ResponseEntity<AppUserDTO> updateAppUser(@RequestBody AppUserDTO appUserDTO,
                                                    @PathVariable("userId") String appUserId) {
        AppUser appUser = appUserMapper.toEntity(appUserDTO);
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
    public ResponseEntity<AppUserDTO> logout(@PathVariable("userId") String appUserId) {
        AppUser appUser = appUserService.logout(appUserId);
        return ResponseEntity.ok(appUserMapper.toDTO(appUser));
    }

    @GetMapping("/check-if/{username}/exists")
    public ResponseEntity<Boolean> checkIfExists(@PathVariable("username") String username) {
        return ResponseEntity.ok(appUserService.checkIfUsernameExists(username));
    }

    @GetMapping("/find-by-username/{username}")
    public ResponseEntity<AppUserDTO> findByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(appUserMapper.toDTO(appUserService.findByUsernameOrEmail(username,username)));
    }


    @PatchMapping("/change-password/{id}")
    public ResponseEntity<AppUserDTO> changePassword(@PathVariable String id, @RequestBody String newPassword) {
        return ResponseEntity.ok(appUserMapper.toDTO(appUserService.changePassword(id, newPassword)));
    }

    @PatchMapping("/change-app-roles/{id}")
    public ResponseEntity<AppUserDTO> changeAppRoles(@PathVariable String id, @RequestBody List<AppRoleDTO> newRoles) {
        return ResponseEntity.ok(appUserMapper.toDTO(appUserService.changeAppRoles(id, appRoleMapper.toEntity(newRoles))));
    }

    @PatchMapping("/change-credentials/{id}")
    public ResponseEntity<AppUserDTO> changeCredentials(@PathVariable String id, @RequestBody CredentialDTO credentialDTO) {
        return ResponseEntity.ok(appUserMapper.toDTO(appUserService.changeCredential(id, credentialDTO.getUsername(), credentialDTO.getEmail())));
    }

    @GetMapping("/count/all")
    public ResponseEntity<Long> countAll() {
        return ResponseEntity.ok(appUserService.countAll());
    }

    @GetMapping("/find/all")
    public ResponseEntity<List<AppUserDTO>> findAllAppUsers() {
        return ResponseEntity.ok(appUserMapper.toDTO(appUserService.findAllAppUser()));
    }
}