package com.taurustechnology.backend.repositories;

import com.taurustechnology.backend.models.LicenseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseModelRepository extends JpaRepository<LicenseModel, String> {
}
