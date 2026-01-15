package com.taurustechnology.backend.repositories;


import com.taurustechnology.backend.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, String> {
    Optional<AppUser> findByAppRoles_name(String appRolesName);

    boolean existsByUsername(String defaultAdminUsername);

    boolean existsByEmail(String defaultAdminEmail);

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByEmail(String email);

    @Query("SELECT a FROM AppUser a WHERE :name IN (SELECT r.name FROM a.appRoles r)")
    List<AppUser> findByRoleName(@Param("name") String roleName);
}
