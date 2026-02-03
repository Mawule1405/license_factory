package com.taurustechnology.backend.services;


import com.taurustechnology.backend.entities.AppRole;
import com.taurustechnology.backend.entities.AppUser;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AppUserService {

    AppUser create(AppUser appUser, String createByAppUserId);
    AppUser findByUsernameOrEmail(String username, String email);
    AppUser findById(String id);
    AppUser update(AppUser appUser, String updateByAppUserId);
    AppUser login(String appUserId);
    AppUser logout(String appUserId);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean deleteById(String id, String deletedByAppUserId);

    List<AppUser> findAdministrators();

    Boolean checkIfUsernameExists(String username);

    AppUser changePassword(String id, String newPassword);

    AppUser changeCredential(String id, String username, String email);

    long countAll();

    AppUser changeAppRoles(String id, List<AppRole> newRoles);

    List<AppUser> findAllAppUser();

    Page<AppUser> searchUsers(String keyword, int page, int size);
}
