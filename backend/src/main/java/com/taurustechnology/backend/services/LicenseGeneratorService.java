package com.taurustechnology.backend.services;

import com.taurustechnology.backend.dtos.requests.LicenseRequest;
import com.taurustechnology.backend.models.License;

public interface LicenseGeneratorService {
    String buildLicense(License request) throws Exception;
}
