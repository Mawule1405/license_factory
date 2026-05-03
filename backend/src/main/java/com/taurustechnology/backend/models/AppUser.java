package com.taurustechnology.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "app_users",
        indexes = {
                @Index(name = "idx_username", columnList = "username"),
                @Index(name = "idx_email", columnList = "email")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Length(min = 3, max = 50, message = "Le nom d'utilisateur doit faire entre 3 et 50 caractères")
    private String username;

    private String fullName;

    @Column(nullable = false)
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Length(min = 8, message = "Le mot de passe doit faire au moins 8 caractères")
    private String password;

    @Column(unique = true, length = 255)
    @Email(message = "Format d'email invalide")
    private String email;

    @Builder.Default
    @Column(nullable = false)
    private boolean loggedIn = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean activated = true; // Par défaut à true ou selon ta logique métier

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_app_roles",
            joinColumns = @JoinColumn(name = "app_user_id"),
            inverseJoinColumns = @JoinColumn(name = "app_role_id")
    )
    @Builder.Default
    private List<AppRole> appRoles = new ArrayList<>();

    // Changement en LAZY pour la performance
    @OneToMany(mappedBy = "register", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Client> clients = new ArrayList<>();


    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
    @Builder.Default
    private List<License> licenses = new ArrayList<>();


    @OneToMany(mappedBy = "register", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Export> Exports = new ArrayList<>();

    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Project> projects = new ArrayList<>();


    @Transient
    private long clientCount;

    @Transient
    private long licenseCount;

    @Transient
    private long exportCount;

    @Transient
    private long projectCount;

    /**
     * Vérifie si l'utilisateur a le droit de se connecter.
     */
    public boolean canLogin() {
        // L'utilisateur doit être activé ET ne pas être marqué comme supprimé (Soft Delete)
        return this.activated && !(this.getDeletedAt() == null);
    }
}