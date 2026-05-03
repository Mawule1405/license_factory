package com.taurustechnology.backend.services.impl;

import com.taurustechnology.backend.dtos.ClientMiniStats;
import com.taurustechnology.backend.models.AppUser;
import com.taurustechnology.backend.models.Client;
import com.taurustechnology.backend.repositories.AppUserRepository;
import com.taurustechnology.backend.repositories.ClientRepository;
import com.taurustechnology.backend.repositories.LicenseRepository;
import com.taurustechnology.backend.services.AuditService;
import com.taurustechnology.backend.services.ClientService;
import com.taurustechnology.backend.specifications.ClientSpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final LicenseRepository licenseRepository;
    private final AppUserRepository appUserRepository;
    private final AuditService auditService;

    @Override
    @Transactional
    public Client save(String username, Client client) {
        log.debug("[START] Saving new client: {} by user: {}", client.getName(), username);

        // Recherche par username (Principal)
        AppUser registrar = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Registrar not found: " + username));

        client.setRegister(registrar);


        try {
            Client savedClient = clientRepository.save(client);
            log.info("[SUCCESS] Client saved with ID: {}", savedClient.getId());

            auditService.logAction("CREATE_CLIENT", username, savedClient.getName(), "SUCCESS");
            return savedClient;
        } catch (Exception e) {
            log.error("[ERROR] Failed to save client", e);
            auditService.logAction("CREATE_CLIENT", username, client.getName(), "FAILED");
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Client> findClients(String username, int page, int size, String searchKey) {
        log.debug("[QUERY] Listing clients with filter: '{}' for user: {}", searchKey, username);

        // On vérifie que l'utilisateur demandeur existe toujours
        if (!appUserRepository.existsByUsername(username)) {
            throw new EntityNotFoundException("Access Denied: User not found");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return clientRepository.findAllByNameContainingIgnoreCase(searchKey, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Client findClient(String username, String id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client record not found: " + id));
    }

    @Override
    @Transactional
    public boolean deleteClient(String username, String id) {
        log.warn("[DELETE] Request to remove client ID: {} by: {}", id, username);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        // Sécurité : Ne pas supprimer si des licences sont rattachées
        if (client.getLicenses() == null || client.getLicenses().isEmpty()) {
            clientRepository.delete(client);

            log.info("[COMPLETED] Client {} permanently removed", client.getName());
            auditService.logAction("DELETE_CLIENT", username, client.getName(), "SUCCESS");
            return true;
        } else {
            log.error("[REJECTED] Cannot delete client {}: Active licenses found", client.getName());
            auditService.logAction("DELETE_CLIENT", username, client.getName(), "REJECTED_HAS_LICENSES");
            return false;
        }
    }

    @Override
    @Transactional
    public Client updateClient(String username, String id, Client clientData) {
        log.info("[UPDATE] Modifying metadata for client ID: {} by: {}", id, username);

        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Target client not found"));

        // Mise à jour sélective
        existingClient.setName(clientData.getName());
        existingClient.setEmail(clientData.getEmail());
        existingClient.setAddress(clientData.getAddress());
        existingClient.setPhone(clientData.getPhone());

        try {
            Client updated = clientRepository.save(existingClient);
            auditService.logAction("UPDATE_CLIENT", username, updated.getName(), "SUCCESS");
            return updated;
        } catch (Exception e) {
            log.error("[ERROR] Metadata sync failed for client {}", id, e);
            auditService.logAction("UPDATE_CLIENT", username, id, "FAILED");
            throw e;
        }
    }

    @Override
    public ClientMiniStats getMiniStats() {
        long totalClients = clientRepository.count();

        // --- Calcul des périodes ---
        LocalDateTime now = LocalDateTime.now();

        // Mois en cours : du 1er du mois à 00:00 jusqu'à maintenant
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);

        // Mois dernier : du 1er au dernier jour du mois précédent
        LocalDateTime startOfLastMonth = now.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        LocalDateTime endOfLastMonth = now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);

        // --- Appels via Specifications ---
        long totalThisMonth = clientRepository.count(ClientSpecifications.createdBetween(startOfMonth, now));
        long totalLastMonth = clientRepository.count(ClientSpecifications.createdBetween(startOfLastMonth, endOfLastMonth));

        // --- Reste de la logique ---
        double growthRate = calculateGrowthRate(totalThisMonth, totalLastMonth);
        double deploymentDensity = totalClients > 0 ? (double) licenseRepository.count() / totalClients : 0;

        String lastDeployed = licenseRepository.findTopByOrderByCreatedAtDesc()
                .map(l -> l.getClient().getName()).orElse("N/A");

        String topProject = licenseRepository.findMostLicensedProjectName();
        String leadArchitect = clientRepository.findTopCreatorName().orElse("N/A");

        long clientsWithLicense = clientRepository.countClientsWithAtLeastOneLicense();
        double conversionEfficiency = totalClients > 0 ? (double) clientsWithLicense / totalClients * 100 : 0;

        return new ClientMiniStats(
                totalClients,
                totalThisMonth,
                growthRate,
                0, // activeDeployments (à remplir selon tes besoins)
                deploymentDensity,
                lastDeployed,
                topProject,
                leadArchitect,
                conversionEfficiency
        );
    }

    private double calculateGrowthRate(long current, long previous) {
        if (previous == 0) return current > 0 ? 100.0 : 0.0;
        return ((double) (current - previous) / previous) * 100;
    }
}