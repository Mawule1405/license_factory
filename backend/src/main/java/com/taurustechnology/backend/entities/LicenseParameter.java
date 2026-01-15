package com.taurustechnology.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LicenseParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyName;  // ex: "MAX_USERS", "MODULE_ANALYTICS"
    private String value;    // ex: "10", "true"
    private String dataType; // ex: "INTEGER", "BOOLEAN" (pour le cast futur)

    @ManyToOne
    @JoinColumn(name = "license_id")
    private License license;

}
