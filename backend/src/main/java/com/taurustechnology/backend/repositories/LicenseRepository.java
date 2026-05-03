package com.taurustechnology.backend.repositories;

import com.taurustechnology.backend.models.License;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseRepository extends JpaRepository<License, String> {
    Page<License> findAllByClient_Id(String clientId, Pageable pageable);
}
