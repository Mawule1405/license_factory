package com.taurustechnology.backend.services.impl;

import com.taurustechnology.backend.models.Export;
import com.taurustechnology.backend.repositories.ExportRepository;
import com.taurustechnology.backend.services.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final ExportRepository exportRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Export> findAll(int page, int size) {
        // Tri par date de création décroissante pour voir les derniers exports en premier
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return exportRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Export> findAllByLicense(String licenseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return exportRepository.findByLicense_Id(licenseId, pageable);
    }
}