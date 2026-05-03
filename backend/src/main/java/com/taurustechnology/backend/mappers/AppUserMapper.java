package com.taurustechnology.backend.mappers;

import com.taurustechnology.backend.dtos.requests.AppUserRequest;
import com.taurustechnology.backend.dtos.responses.AppUserResponse;
import com.taurustechnology.backend.models.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AppRoleMapper.class})
public interface AppUserMapper {

    // On mappe les champs de base + les expressions personnalisées
    @Mapping(target = "clientCount", expression = "java(countClients(user))")
    @Mapping(target = "licenseCount", expression = "java(countLicenses(user))")
    @Mapping(target = "projectCount", expression = "java(countProjects(user))")
    @Mapping(target = "exportCount", expression = "java(countExports(user))")
    // MapStruct gère automatiquement les noms identiques (username, email, etc.)
    AppUserResponse toDTO(AppUser user);

    AppUser toEntity(AppUserRequest userRequest);

    List<AppUserResponse> toDTO(List<AppUser> users);

    // Méthodes par défaut pour sécuriser le comptage
    default long countClients(AppUser user) {
        return (user.getClients() != null) ? user.getClients().size() : 0;
    }

    default long countLicenses(AppUser user) {
        // Vérifie l'orthographe exacte dans ton entité (License ou Licence)
        return (user.getLicenses() != null) ? user.getLicenses().size() : 0;
    }

    default long countExports(AppUser user) {
        return (user.getExports() != null) ? user.getExports().size() : 0;
    }

    default long countProjects(AppUser user) {
        return (user.getProjects() != null) ? user.getProjects().size() : 0;
    }
}