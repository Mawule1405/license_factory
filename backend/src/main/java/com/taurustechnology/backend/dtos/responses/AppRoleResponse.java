package com.taurustechnology.backend.dtos.responses;

import jakarta.persistence.Column;
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
public class AppRoleResponse {

    private String id;
    private String name;
    private String description;

}