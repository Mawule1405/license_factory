package com.taurustechnology.backend.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserRequest {

    private String id;

    @Column( nullable = false, unique = true, length = 50)
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Length(min = 3, max = 50, message = "Le nom d'utilisateur doit faire entre 3 et 50 caractères")
    private String username;

    private String fullName;

    @Column(nullable = false)
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Length(min = 8, message = "Le mot de passe doit faire au moins 8 caractères")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;


    @Column(unique = true, length = 255)
    @Email(message = "Format d'email invalide")
    private String email;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean activated = true;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean deleted = false;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean loggedIn = false;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt = LocalDateTime.now();

    private List<AppRoleRequest> appRoles = new ArrayList<>();


}