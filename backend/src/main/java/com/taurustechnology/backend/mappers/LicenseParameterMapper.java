package com.taurustechnology.backend.mappers;

import com.taurustechnology.backend.dtos.LicenseParameterDto;
import com.taurustechnology.backend.models.LicenseParameter;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LicenseParameterMapper {

    LicenseParameterDto toDto(LicenseParameter licenseParameter);
    LicenseParameter toEntity(LicenseParameterDto licenseParameterDto);
    List<LicenseParameterDto> toDto(List<LicenseParameter> licenseParameterList);
    List<LicenseParameter> toEntity(List<LicenseParameterDto> licenseParameterDtoList);

}
