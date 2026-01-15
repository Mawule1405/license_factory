package com.taurustechnology.backend.mappers;


import com.taurustechnology.backend.dtos.AppRoleDTO;
import com.taurustechnology.backend.entities.AppRole;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppRoleMapper {
    AppRoleDTO toDTO(AppRole appRole);
    AppRole toEntity(AppRoleDTO appRoleDTO);
    List<AppRoleDTO> toDTO(List<AppRole> appRoles);
    List<AppRole> toEntity(List<AppRoleDTO> appRoleDTOs);
}
