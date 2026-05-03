package com.taurustechnology.backend.repositories;


import com.taurustechnology.backend.dtos.responses.AppUserResponse;
import com.taurustechnology.backend.models.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, String> {
    Optional<AppUser> findByAppRoles_name(String appRolesName);

    boolean existsByUsername(String defaultAdminUsername);

    boolean existsByEmail(String defaultAdminEmail);

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByEmail(String email);

    @Query("SELECT a FROM AppUser a WHERE :name IN (SELECT r.name FROM a.appRoles r)")
    Page<AppUser> findByRoleName(@Param("name") String roleName, Pageable pageable);

    @Query("SELECT new com.taurustechnology.backend.dtos.responses.AppUserResponse(" +
            "u.id, u.username, u.fullName, u.email, u.activated, u.loggedIn, u.createdAt, u.updatedAt, " +
            "(SELECT COUNT(c) FROM Client c WHERE c.register = u), " +
            "(SELECT COUNT(l) FROM License l WHERE l.creator = u), " +
            "(SELECT COUNT(e) FROM Export e WHERE e.register = u), " +
            "(SELECT COUNT(p) FROM Project p WHERE p.creator = u)) " +
            "FROM AppUser u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :kw, '%'))")
    Page<AppUserResponse> searchOperatorsWithStats(@Param("kw") String keyword, Pageable pageable);

    Optional<AppUser> findByUsernameOrEmail(String username, String email);
}
