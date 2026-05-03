package com.taurustechnology.backend.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exports") // "exports" est souvent plus standard que "exportation"
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Export extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private AppUser register;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "license_id", nullable = false)
    private License license;

    @Column(columnDefinition = "TEXT")
    private String details;
}