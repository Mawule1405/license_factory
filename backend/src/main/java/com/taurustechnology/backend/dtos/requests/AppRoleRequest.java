package com.taurustechnology.backend.dtos.requests;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AppRoleRequest {


    private String id;

    @Column( nullable = false, unique = true, length = 50)
    @NotBlank(message = "Le nom du rôle est obligatoire")
    @Length(min = 2, max = 50, message = "Le nom doit faire entre 2 et 50 caractères")
    private String name;

    @Length(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;



}