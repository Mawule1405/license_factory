package com.taurustechnology.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseParameter extends JpaRepository<LicenseParameter, String> {
}
