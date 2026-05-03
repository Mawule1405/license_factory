package com.taurustechnology.backend.services;

import com.taurustechnology.backend.models.Export;
import org.springframework.data.domain.Page;

public interface ExportService {

    Page<Export> findAll(int page, int size);
    Page<Export> findAllByLicense(String licenseId, int page, int size);
}
