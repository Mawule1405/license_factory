package com.taurustechnology.backend.dtos;

import lombok.Data;

@Data
public class LicenseRequest {
    private String customerName;
    private String address;
    private String phone;
    private String licenseLevel; // FREEMIUM, BASIC, etc.
    private int maxUsers;
    private int maxDocuments;
    private int maxFolders;
    private String expiryDate;
}