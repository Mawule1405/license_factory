package com.taurustechnology.backend.repositories;

import com.taurustechnology.backend.models.Export;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportRepository extends JpaRepository<Export, String> {
    Page<Export> findByLicenseId(String licenseId, Pageable pageable);
}
