package com.taurustechnology.backend.mappers;

import com.taurustechnology.backend.dtos.LicenseDTO;
import com.taurustechnology.backend.entities.License;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LicenseMapper {

    @Mapping(target = "creatorId", source = "creator.id")
    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target="level", source="licenseLevel" )
    LicenseDTO toDto(License entity);

    @Mapping(source = "creatorId", target = "creator.id")
    @Mapping(source = "clientId", target = "client.id")
    @Mapping(source = "level", target = "licenseLevel")
    License toEntity(LicenseDTO dto);
}
