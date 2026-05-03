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
    @Mapping(target = "parameters", expression = "java(mapParametersToMap(entity.getParameters()))")
    LicenseResponse toResponse(License entity);

    @Mapping(target = "parameters", expression = "java(mapParametersToList(dto.getParameters()))")
    License toEntity(LicenseRequest dto);

    /**
     * Convertit la liste d'entités LicenseParameter en Map pour le DTO de réponse
     */
    default Map<String, String> mapParametersToMap(List<LicenseParameter> parameters) {
        if (parameters == null) return null;
        return parameters.stream()
                .collect(Collectors.toMap(
                        LicenseParameter::getLabel,
                        LicenseParameter::getValue,
                        (existing, replacement) -> existing
                ));
    }

    /**
     * Convertit la Map du DTO en Liste d'entités pour la persistance
     */
    default List<LicenseParameter> mapParametersToList(Map<String, String> parameters) {
        if (parameters == null) return null;
        return parameters.entrySet().stream()
                .map(entry -> LicenseParameter.builder()
                        .label(entry.getKey())
                        .value(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }
}