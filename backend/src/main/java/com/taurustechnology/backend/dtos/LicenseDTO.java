package com.taurustechnology.backend.entities;

import com.taurustechnology.backend.enums.LicenseLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class License {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String licenseKey;
    private String addressMac;
    @Enumerated(EnumType.STRING)
    private LicenseLevel niveau;
    private long maxUsers;

    private LocalDateTime createdAt;
    private LocalDateTime expiryDate;

    private boolean activated;
    private boolean deleted;

    @ManyToOne
    private Client client;

    @ManyToOne
    private AppUser creator;

}
