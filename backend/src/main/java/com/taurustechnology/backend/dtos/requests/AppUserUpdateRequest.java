package com.taurustechnology.backend.dtos.requests;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data @NoArgsConstructor
@AllArgsConstructor
public class AppUserUpdateRequest {

    @Column(nullable = false)
    private  String id;

    @Column( nullable = false, unique = true, length = 50)
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Length(min = 3, max = 50, message = "Le nom d'utilisateur doit faire entre 3 et 50 caractères")
    private String username;


    private String fullName;


    @Column(unique = true, length = 255)
    @Email(message = "Format d'email invalide")
    private String email;
}
