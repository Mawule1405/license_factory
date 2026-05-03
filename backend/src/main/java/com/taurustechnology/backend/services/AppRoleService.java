package com.taurustechnology.backend.services;


import com.taurustechnology.backend.models.AppRole;

import java.util.List;
import java.util.Optional;

public interface AppRoleService {

    AppRole save(AppRole appRole);
    Optional<AppRole> findById(String id);
    List<AppRole> findAll();

    boolean existsByName(String name);

    void deleteById(String id);
}
