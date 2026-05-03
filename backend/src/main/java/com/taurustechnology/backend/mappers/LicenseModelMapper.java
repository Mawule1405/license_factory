package com.taurustechnology.backend.mappers;

import com.taurustechnology.backend.dtos.responses.LicenseModelResponse;
import com.taurustechnology.backend.models.LicenseModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LicenseModelMapper {
    LicenseModelResponse toDto(LicenseModel licenseModel);

}
