package com.taurustechnology.backend.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseStatus {

    // État de la licence
    private boolean valid;
    private String errorMessage; // Pour stocker pourquoi la licence est invalide

    // Informations du client
    private String customerName;
    private String address;
    private String phone;

    // Paramètres de l'offre
    private String licenseLevel; // FREEMIUM, BASIC, CLASSIC, PREMIUM
    private String expiryDate;   // Format YYYY-MM-DD

    // Limites techniques (Quotas)
    private Long maxUsers;
    private String addressMac;


    /**
     * Méthode utilitaire pour vérifier si la licence est expirée
     * par rapport à la date du jour.
     */
    public boolean isExpired() {
        if (expiryDate == null || expiryDate.isEmpty()) return true;
        try {
            LocalDate expiration = LocalDate.parse(expiryDate);
            return LocalDate.now().isAfter(expiration);
        } catch (Exception e) {
            return true;
        }
    }
}
