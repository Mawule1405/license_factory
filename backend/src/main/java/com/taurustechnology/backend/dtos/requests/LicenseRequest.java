package com.taurustechnology.backend.dtos.requests;




import lombok.Data;

import java.util.Map;

@Data
public class LicenseRequest {
     private String clientId;
     private String projectId;
     private Map<String , String> parameters;
}

