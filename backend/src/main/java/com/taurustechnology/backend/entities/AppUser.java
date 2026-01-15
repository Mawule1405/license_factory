package com.taurustechnology.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "app_users",
        indexes = {

        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column( nullable = false)
    private String id;


    @Column( nullable = false, unique = true, length = 50)
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Length(min = 3, max = 50, message = "Le nom d'utilisateur doit faire entre 3 et 50 caractères")
    private String username;


    @Column(nullable = false)
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Length(min = 8, message = "Le mot de passe doit faire au moins 8 caractères")
    private String passwordHash;


    @Column(unique = true, length = 255)
    @Email(message = "Format d'email invalide")
    private String email;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean activated = true;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean deleted = false;

    @Column( nullable = false, columnDefinition = "boolean default false")
    private boolean loggedIn = false;

    // Audit fields
    @CreatedDate
    @Column( updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.now();


    // Relations
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<AppRole> appRoles = new ArrayList<>();


    public void addRole(AppRole role) {
        appRoles.add(role);
    }

    public boolean canLogin() {
        return !deleted && activated;
    }
}