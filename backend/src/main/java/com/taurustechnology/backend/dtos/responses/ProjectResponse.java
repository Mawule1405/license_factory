package com.taurustechnology.backend.dtos.responses;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectResponse  {

    private String id;
    private String name;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String creatorName;

    private LicenseModelResponse licenseModel;

}