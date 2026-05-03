package com.taurustechnology.backend.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserResponse {

    private String id;

    private String username;

    private String fullName;

    private String email;

    private Boolean activated;

    private Boolean loggedIn;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Utilisation du DTO pour les rôles
    @Builder.Default
    private List<AppRoleResponse> roles = new ArrayList<>();

    // Noms d'attributs corrigés et harmonisés
    private long clientCount;

    private long licenseCount;

    private long exportCount;

    private long projectCount;

    public AppUserResponse(String id, String username, String fullName, String email,
                           boolean activated,  boolean loggedIn,
                           LocalDateTime createdAt, LocalDateTime updatedAt,
                           long clientCount, long licenseCount, long exportCount, long projectCount) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.activated = activated;
        this.loggedIn = loggedIn;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.clientCount = clientCount;
        this.licenseCount = licenseCount;
        this.exportCount = exportCount;
        this.projectCount = projectCount;
    }

}