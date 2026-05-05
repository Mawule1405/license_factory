package com.taurustechnology.backend.dtos.requests;




import com.taurustechnology.backend.models.Parameter;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class LicenseRequest {
     private String clientId;
     private String projectId;
     private List<Parameter> parameters = new ArrayList<>();
}

