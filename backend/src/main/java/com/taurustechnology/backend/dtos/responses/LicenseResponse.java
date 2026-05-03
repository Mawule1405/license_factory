package com.taurustechnology.backend.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseResponse {

    private String id;

    // Informations de base
    private String activationCode;
    private boolean active;
    private LocalDateTime createdAt;

    // Détails du Client (pour l'affichage dans la liste)
    private String clientId;
    private String clientName;
    private String clientEmail;

    // Détails du Projet
    private String projectId;
    private String projectName;

    // Utilisateur ayant généré la licence
    private String creatorName;

    /**
     * Les paramètres dynamiques transformés en Map<Label, Value>
     * Exemple: { "max_users": "10", "environment": "production" }
     */
    private Map<String, String> parameters;
}