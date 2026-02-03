package com.taurustechnology.backend.services;

import com.taurustechnology.backend.dtos.LicenseRequest;
import com.taurustechnology.backend.entities.License;

public interface LicenseGeneratorService {
    String buildLicense(LicenseRequest request) throws Exception;

    String buildLicense(License request) throws Exception;
}
