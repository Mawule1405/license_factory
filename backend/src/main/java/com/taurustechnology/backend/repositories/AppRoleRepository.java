package com.taurustechnology.backend.repositories;


import com.taurustechnology.backend.models.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppRoleRepository extends JpaRepository<AppRole, String> {

    boolean existsByName(String name);

    AppRole findByName(String name);
}
