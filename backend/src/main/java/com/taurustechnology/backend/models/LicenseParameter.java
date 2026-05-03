package com.taurustechnology.backend.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "license_parameters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LicenseParameter extends BaseEntity {

    @Column(nullable = false)
    private String label;

    @Column(name = "parameter_value", nullable = false)
    private String value; // Ex: "10", "true"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "license_id", nullable = false)
    private License license;
}