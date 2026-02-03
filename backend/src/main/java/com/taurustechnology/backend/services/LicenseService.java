package com.taurustechnology.backend.services;

import com.taurustechnology.backend.entities.License;
import org.springframework.data.domain.Page;

public interface LicenseService {

    License save(String userId, License license);
    Page<License> findAll(String userId, int page, int size);
    License findOne(String userId, String id);
    License update(String userId, License license);
    boolean delete(String userId, String id);
    String generateLicense(String userId, String id);
}
