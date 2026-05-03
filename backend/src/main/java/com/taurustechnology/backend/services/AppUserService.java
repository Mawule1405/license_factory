package com.taurustechnology.backend.services;


import com.taurustechnology.backend.dtos.responses.AppUserResponse;
import com.taurustechnology.backend.models.AppRole;
import com.taurustechnology.backend.models.AppUser;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AppUserService {

    AppUser create(AppUser appUser, String username);
    AppUser findByUsernameOrEmail(String username, String email);
    AppUser findById(String id);
    AppUser update(AppUser appUser, String updateByAppUserId);

    AppUser changePassword(String userId, String oldPassword, String newPassword);

    AppUser login(String appUserId);
    AppUser logout(String appUserId);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean deleteById(String id, String deletedByAppUserId);

    Page<AppUser> findAdministrators(int page, int size);

    Boolean checkIfUsernameExists(String username);

    AppUser initialize(String initializerId, String userId, String newPassword);

    AppUser changeCredential(String id, String username, String email);

    long countAll();

    AppUser changeAppRoles(String id, List<AppRole> newRoles);

    Page<AppUser> findAllAppUser(int page, int size);

    Page<AppUserResponse> searchUsers(String keyword, int page, int size);
}
