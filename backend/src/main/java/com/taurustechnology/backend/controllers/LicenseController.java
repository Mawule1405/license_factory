package com.taurustechnology.backend.controllers;

import com.taurustechnology.backend.dtos.LicenseRequest;
import com.taurustechnology.backend.services.impl.LicenseGeneratorServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/licenses")
@CrossOrigin("*") // Pour permettre à ton Angular de communiquer
public class LicenseController {

    private final LicenseGeneratorServiceImpl licenseService;

    public LicenseController(LicenseGeneratorServiceImpl licenseService) {
        this.licenseService = licenseService;
    }

    @PostMapping("/generate")
    public ResponseEntity<byte[]> downloadLicense(@RequestBody LicenseRequest request) {
        try {
            String licenseContent = licenseService.buildLicense(request);
            byte[] licenseBytes = licenseContent.getBytes(StandardCharsets.UTF_8);

            String fileName = request.getCustomerName().replaceAll("\\s+", "_") + ".lic";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(licenseBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}