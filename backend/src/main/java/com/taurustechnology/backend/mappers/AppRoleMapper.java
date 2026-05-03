package com.taurustechnology.backend.mappers;


import com.taurustechnology.backend.dtos.requests.AppRoleRequest;
import com.taurustechnology.backend.dtos.responses.AppRoleResponse;
import com.taurustechnology.backend.models.AppRole;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppRoleMapper {
    AppRoleResponse toDTO(AppRole appRole);
    AppRole toEntity(AppRoleRequest appRoleRequest);
    List<AppRoleResponse> toDTO(List<AppRole> appRoles);
    List<AppRole> toEntity(List<AppRoleRequest> appRoleRequests);
}
