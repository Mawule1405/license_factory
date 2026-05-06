package com.taurustechnology.backend.mappers;

import com.taurustechnology.backend.dtos.requests.LicenseRequest;
import com.taurustechnology.backend.dtos.responses.LicenseResponse;
import com.taurustechnology.backend.models.License;
import com.taurustechnology.backend.models.LicenseParameter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface LicenseMapper {

    @Mapping(target = "clientId", expression = "java(entity.getClient().getId())")
    @Mapping(target = "clientName", source = "client.name")
    @Mapping(target = "clientEmail", source = "client.email")
    @Mapping(target = "projectId", expression = "java(entity.getProject().getId())")
    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "creatorName", source = "creator.username")
    LicenseResponse toResponse(License entity);


    License toEntity(LicenseRequest dto);



}