package com.taurustechnology.backend.mappers;


import com.taurustechnology.backend.dtos.ClientDTO;
import com.taurustechnology.backend.entities.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ClientMapper {


    @Mappings({
            @Mapping(target = "creatorId", source = "creator.id"),
            @Mapping(target = "numberOfLicenses",
                    expression = "java(client.getLicenses()!=null?client.getLicenses().size():0L)")
    })
    ClientDTO toDto(Client client);

    @Mappings({
            @Mapping(target = "creator.id", source = "creatorId"),
    })
    Client toEntity(ClientDTO clientDTO);
}
