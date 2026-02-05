package com.taurustechnology.backend.services.impl;

import com.taurustechnology.backend.entities.AppRole;
import com.taurustechnology.backend.entities.AppUser;
import com.taurustechnology.backend.repositories.AppRoleRepository;
import com.taurustechnology.backend.repositories.AppUserRepository;
import com.taurustechnology.backend.services.AppUserService;
import com.taurustechnology.backend.services.AuditService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppRoleRepository appRoleRepository;
    private final AuditService auditService;

    @Override
    public AppUser create(AppUser appUser, String createdByAppUserId) {
        if (appUser == null) throw new IllegalArgumentException("AppUser cannot be null");

        try {
            Optional<AppUser> creator = appUserRepository.findById(createdByAppUserId);
            if (creator.isEmpty()) {
                log.warn("Creator {} not found", createdByAppUserId);
                auditService.logAction("CREATE_USER", createdByAppUserId, appUser.getUsername(), "FAILED: CREATOR_NOT_FOUND");
                return null;
            }

            if (appUserRepository.existsByUsername(appUser.getUsername())) {
                auditService.logAction("CREATE_USER", createdByAppUserId, appUser.getUsername(), "FAILED: USERNAME_EXISTS");
                throw new IllegalArgumentException("Username already exists");
            }

            appUser.setPasswordHash(passwordEncoder.encode(appUser.getPasswordHash()));
            appUser.setCreatedAt(LocalDateTime.now());
            appUser.setUpdatedAt(LocalDateTime.now());
            appUser.setActivated(true);

            AppUser saved = appUserRepository.save(appUser);
            auditService.logAction("CREATE_USER", createdByAppUserId, saved.getUsername(), "SUCCESS");
            log.info("User {} created successfully", saved.getUsername());
            return saved;
        } catch (Exception e) {
            auditService.logAction("CREATE_USER", createdByAppUserId, appUser.getUsername(), "FAILED: " + e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public AppUser changePassword(String userId, String oldPassword, String newPassword) {
        log.debug("Request to change password for user ID: {}", userId);

        AppUser appUser = appUserRepository.findById(userId)
                .orElseThrow(() -> {
                    auditService.logAction("CHANGE_PASSWORD", "SYSTEM", userId, "FAILED: USER_NOT_FOUND");
                    return new EntityNotFoundException("User not found");
                });

        if (!passwordEncoder.matches(oldPassword, appUser.getPasswordHash())) {
            auditService.logAction("CHANGE_PASSWORD", appUser.getUsername(), userId, "FAILED: WRONG_OLD_PASSWORD");
            log.warn("Invalid old password attempt for user: {}", appUser.getUsername());
            throw new IllegalArgumentException("Wrong password");
        }

        appUser.setPasswordHash(passwordEncoder.encode(newPassword));
        appUser.setUpdatedAt(LocalDateTime.now());
        AppUser updated = appUserRepository.save(appUser);

        auditService.logAction("CHANGE_PASSWORD", appUser.getUsername(), userId, "SUCCESS");
        log.info("Password changed for user: {}", appUser.getUsername());
        return updated;
    }

    @Override
    public AppUser update(AppUser appUser, String updatedByAppUserId) {
        AppUser existing = appUserRepository.findById(appUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        try {
            existing.setFullName(appUser.getFullName());
            existing.setEmail(appUser.getEmail());
            existing.setActivated(appUser.isActivated());
            existing.setUpdatedAt(LocalDateTime.now());

            AppUser updated = appUserRepository.save(existing);
            auditService.logAction("UPDATE_PROFILE", updatedByAppUserId, updated.getUsername(), "SUCCESS");
            return updated;
        } catch (Exception e) {
            auditService.logAction("UPDATE_PROFILE", updatedByAppUserId, appUser.getUsername(), "FAILED: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public AppUser login(String appUserId) {
        return appUserRepository.findById(appUserId).map(user -> {
            user.setLoggedIn(true);
            user.setUpdatedAt(LocalDateTime.now());
            auditService.logAction("LOGIN", user.getUsername(), appUserId, "SUCCESS");
            return appUserRepository.save(user);
        }).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public AppUser logout(String appUserId) {
        return appUserRepository.findById(appUserId).map(user -> {
            user.setLoggedIn(false);
            user.setUpdatedAt(LocalDateTime.now());
            auditService.logAction("LOGOUT", user.getUsername(), appUserId, "SUCCESS");
            return appUserRepository.save(user);
        }).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public boolean deleteById(String id, String deletedByAppUserId) {
        return appUserRepository.findById(id).map(user -> {
            user.setDeleted(true);
            user.setUpdatedAt(LocalDateTime.now());
            appUserRepository.save(user);
            auditService.logAction("SOFT_DELETE", deletedByAppUserId, user.getUsername(), "SUCCESS");
            return true;
        }).orElseGet(() -> {
            auditService.logAction("SOFT_DELETE", deletedByAppUserId, id, "FAILED: NOT_FOUND");
            return false;
        });
    }

    @Override
    public AppUser changeCredential(String id, String username, String email) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setUsername(username);
        user.setEmail(email);
        user.setUpdatedAt(LocalDateTime.now());

        AppUser updated = appUserRepository.save(user);
        auditService.logAction("CHANGE_CREDENTIALS", "ADMIN", updated.getUsername(), "SUCCESS");
        return updated;
    }

    @Override
    public AppUser changeAppRoles(String id, List<AppRole> newRoles) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setAppRoles(newRoles);
        AppUser updated = appUserRepository.save(user);
        auditService.logAction("CHANGE_ROLES", "ADMIN", updated.getUsername(), "SUCCESS");
        return updated;
    }

    // --- READ OPERATIONS (No Audit required usually) ---

    @Override
    public AppUser findByUsernameOrEmail(String username, String email) {
        return appUserRepository.findByUsername(username)
                .or(() -> appUserRepository.findByEmail(email))
                .orElse(null);
    }

    @Override
    public AppUser findById(String id) {
        return appUserRepository.findById(id).orElse(null);
    }

    @Override
    public List<AppUser> findAdministrators() {
        return appUserRepository.findByRoleName("ADMINISTRATEUR");
    }

    @Override
    public Page<AppUser> searchUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fullName"));
        return appUserRepository.searchOperators(keyword, pageable);
    }

    @Override
    public long countAll() {
        return appUserRepository.count();
    }

    @Override
    public boolean existsByUsername(String username) {
        return appUserRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return appUserRepository.existsByEmail(email);
    }

    @Override
    public Boolean checkIfUsernameExists(String username) {
        return appUserRepository.existsByUsername(username);
    }

    @Override
    public AppUser initialize(String initializerId, String userId, String newPassword) {
        AppUser user = appUserRepository.findById(userId).orElseThrow();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        auditService.logAction("INITIALIZE_PASSWORD", initializerId, user.getUsername(), "SUCCESS");
        return appUserRepository.save(user);
    }

    @Override
    public List<AppUser> findAllAppUser() {
        return appUserRepository.findAll();
    }
}