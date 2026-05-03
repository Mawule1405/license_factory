package com.taurustechnology.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO {

    private String id;

    private String name;
    private String email;

    private String address;
    private String phone;

    private LocalDateTime createdAt;

    private String creatorId;
    @ReadOnlyProperty
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long numberOfLicenses;
}