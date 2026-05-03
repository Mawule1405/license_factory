package com.taurustechnology.backend.mappers;

import com.taurustechnology.backend.dtos.requests.ProjectRequest;
import com.taurustechnology.backend.dtos.responses.ProjectResponse;
import com.taurustechnology.backend.models.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {LicenseModelMapper.class})
public interface ProjectMapper {

    @Mapping(target = "creatorName", source = "creator.fullName")
    ProjectResponse toDto(Project project);
    Project toEntity(ProjectRequest project);

}
