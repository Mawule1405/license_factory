package com.taurustechnology.backend.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "audit_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class History extends BaseEntity {

    @Column(nullable = false)
    private String action; // Ex: "CREATE_USER", "UPDATE_LICENSE", "DELETE_CLIENT"

    @Column(nullable = false)
    private String entityName; // L'entité concernée : "AppUser", "License", "Project"

    private String entityId; // L'ID de l'objet modifié (ton UUID)

    @Column(columnDefinition = "TEXT")
    private String description; // Détails : "L'admin a changé le niveau de la licence X"

    private String ipAddress; // Optionnel : pour la sécurité réseau

}