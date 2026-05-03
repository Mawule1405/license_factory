package com.taurustechnology.backend.repositories;

import com.taurustechnology.backend.models.LicenseParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseParameterRepository extends JpaRepository<LicenseParameter, String> {
}
