package com.taurustechnology.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.taurustechnology.backend.entities.AppUser;
import com.taurustechnology.backend.entities.Client;
import com.taurustechnology.backend.enums.LicenseLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LicenseDTO {

    private String id;

    private String licenseKey;
    private String addressMac;

    private LicenseLevel level;
    private Long maxUsers;

    private LocalDateTime createdAt;
    private LocalDate expiryDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean activated;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean deleted;

    private String clientId;
    private String creatorId;

}
