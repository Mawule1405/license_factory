package com.taurustechnology.backend.dtos;

import com.taurustechnology.backend.models.BaseEntity;
import com.taurustechnology.backend.models.License;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LicenseParameterDto {

    private String id;

    private String label;

    private String value;

    private String type;

}