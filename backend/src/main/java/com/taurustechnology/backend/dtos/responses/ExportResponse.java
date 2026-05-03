package com.taurustechnology.backend.dtos.responses;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportResponse {
    private String id;

    // Informations sur l'administrateur (le "register")
    private String adminId;
    private String adminFullName;

    // Informations sur la licence
    private String licenseId;
    private String activationCode;
    private String clientName;
    private String projectName;

    // Détails de l'exportation
    private String details; // Contient la raison de l'exportation
    private LocalDateTime createdAt;
}