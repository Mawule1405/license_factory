package com.taurustechnology.backend.mappers;

import com.taurustechnology.backend.dtos.AppUserDTO;
import com.taurustechnology.backend.entities.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AppRoleMapper.class})
public interface AppUserMapper {
    AppUserDTO toDTO(AppUser appRole);

    @Mapping(target = "passwordHash", source = "password")
    AppUser toEntity(AppUserDTO appRoleDTO);
    List<AppUserDTO> toDTO(List<AppUser> appRoles);
    List<AppUser> toEntity(List<AppUserDTO> appRoleDTOs);

}
