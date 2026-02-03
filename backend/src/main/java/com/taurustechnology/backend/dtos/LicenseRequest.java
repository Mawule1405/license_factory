package com.taurustechnology.backend.dtos;

import com.taurustechnology.backend.entities.Client;
import com.taurustechnology.backend.enums.LicenseLevel;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LicenseRequest {

    private String id;

    private String licenseKey;
    private String addressMac;
    private LicenseLevel niveau;
    private long maxUsers;

    private LocalDateTime createdAt;
    private LocalDateTime expiryDate;

    private boolean activated;
    private boolean deleted;

    private String clientId;

}