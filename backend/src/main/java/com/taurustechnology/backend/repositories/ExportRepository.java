package com.taurustechnology.backend.repositories;

import com.taurustechnology.backend.models.AppUser;
import com.taurustechnology.backend.models.Export;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportRepository extends JpaRepository<Export, String> {

    Page<Export> findByLicense_Id(String licenseId, Pageable pageable);

    long countByRegister(AppUser register);
}
