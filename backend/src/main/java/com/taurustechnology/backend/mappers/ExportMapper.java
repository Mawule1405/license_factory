package com.taurustechnology.backend.mappers;

import com.taurustechnology.backend.dtos.responses.ExportResponse;
import com.taurustechnology.backend.models.Export;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExportMapper {

    @Mapping(target = "adminId", source = "register.id")
    @Mapping(target = "adminFullName", expression = "java(export.getRegister().getFirstName() + \" \" + export.getRegister().getLastName())")
    @Mapping(target = "licenseId", source = "license.id")
    @Mapping(target = "activationCode", source = "license.activationCode")
    @Mapping(target = "clientName", source = "license.client.name")
    @Mapping(target = "projectName", source = "license.project.name")
    @Mapping(target = "createdAt", source = "createdAt") // Hérité de BaseEntity
    ExportResponse toResponse(Export export);
}