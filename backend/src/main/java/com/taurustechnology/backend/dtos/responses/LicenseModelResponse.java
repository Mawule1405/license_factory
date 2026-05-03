package com.taurustechnology.backend.dtos.responses;


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

    private List<String> parameters = new ArrayList<>();

    private String description;
}