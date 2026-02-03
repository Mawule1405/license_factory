package com.taurustechnology.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor
@AllArgsConstructor
public class LicenseFormat {
    private String customerName;
    private String address;
    private String phone;
    private String licenseLevel;
    private Long maxUsers;
    private String addressMac;
    private String expiryDate;
}
