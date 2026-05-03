package com.taurustechnology.backend.repositories;

import com.taurustechnology.backend.models.AppUser;
import com.taurustechnology.backend.models.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, String>, JpaSpecificationExecutor<Client> {

    Page<Client> findAllByNameContainingIgnoreCase(String searchKey, Pageable pageable);

    long countByRegister(AppUser register);

    @Query("SELECT COUNT(DISTINCT l.client.id) FROM License l")
    long countClientsWithAtLeastOneLicense();

    @Query(value = "SELECT u.username FROM app_users u " +
            "JOIN clients c ON c.registered_by_user_id = u.id " +
            "GROUP BY u.id, u.username " +
            "ORDER BY COUNT(c.id) DESC LIMIT 1", nativeQuery = true)
    Optional<String> findTopCreatorName();
}