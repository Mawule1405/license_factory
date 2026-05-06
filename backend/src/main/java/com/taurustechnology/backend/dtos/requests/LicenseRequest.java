package com.taurustechnology.backend.dtos.requests;




import com.taurustechnology.backend.dtos.LicenseParameterDto;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class LicenseRequest {

     private String clientId;
     private String projectId;
     private List<LicenseParameterDto> parameters;

}

