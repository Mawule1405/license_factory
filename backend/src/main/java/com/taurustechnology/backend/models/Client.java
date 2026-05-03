package com.taurustechnology.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(
        name = "clients",
        indexes = {
                @Index(name = "idx_client_email", columnList = "email"),
                @Index(name = "idx_client_phone", columnList = "phone")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client extends BaseEntity {

    @NotBlank(message = "Le nom du client est obligatoire")
    @Column(nullable = false)
    private String name;

    @Email(message = "Format d'email invalide")
    @Column(unique = true, nullable = false)
    private String email;

    private String address;

    @Column(unique = true)
    private String phone;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<License> licenses;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_by_user_id")
    private AppUser register;
}