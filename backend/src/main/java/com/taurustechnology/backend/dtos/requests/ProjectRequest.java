package com.taurustechnology.backend.dtos.requests;

import com.taurustechnology.backend.dtos.responses.LicenseModelResponse;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectRequest {

    private String name;

    private String description;

    private String creatorId;

    private LicenseModelResponse licenseModel;

}