package com.taurustechnology.backend.mappers;


import com.taurustechnology.backend.dtos.ClientDTO;
import com.taurustechnology.backend.dtos.responses.ClientResponse;
import com.taurustechnology.backend.models.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mappings({
            @Mapping(target="registerName", source="register.fullName")
    })
    ClientResponse toDto(Client client);

    Client toEntity(ClientDTO clientDTO);


}
