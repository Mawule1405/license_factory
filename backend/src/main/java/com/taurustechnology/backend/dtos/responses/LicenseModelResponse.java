package com.taurustechnology.backend.dtos.responses;


import com.taurustechnology.backend.models.Parameter;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LicenseModelResponse {

    private String projectId;

    private String description;

    private List<Parameter> parameters = new ArrayList<>();
}