package com.taurustechnology.backend.services;

import com.taurustechnology.backend.dtos.LicenseRequest;

public interface LicenseGeneratorService {
    String buildLicense(LicenseRequest request) throws Exception;
}
