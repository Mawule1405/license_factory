package com.taurustechnology.backend.services.impl;

import com.taurustechnology.backend.entities.AppUser;
import com.taurustechnology.backend.entities.Client;
import com.taurustechnology.backend.repositories.AppUserRepository;
import com.taurustechnology.backend.repositories.ClientRepository;
import com.taurustechnology.backend.services.AuditService;
import com.taurustechnology.backend.services.ClientService;
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

@Service
@Slf4j
@RequiredArgsConstructor // Génère le constructeur pour l'injection automatique
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final AppUserRepository appUserRepository;
    private final AuditService auditService;

    @Override
    @Transactional
    public Client save(String userId, Client client) {
        log.debug("Attempting to save new client: {}", client.getName());

        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        client.setCreator(user);
        client.setCreatedAt(LocalDateTime.now());

        try {
            Client savedClient = clientRepository.save(client);

            // Log technique
            log.info("Client successfully saved with ID: {}", savedClient.getId());

            // Log d'audit
            auditService.logAction("CREATE_CLIENT", userId, savedClient.getName(), "SUCCESS");

            return savedClient;
        } catch (Exception e) {
            log.error("Database error while saving client", e);
            auditService.logAction("CREATE_CLIENT", userId, client.getName(), "FAILED");
            throw e;
        }
    }

    @Override
    public Page<Client> findClients(String userId, int page, int size, String searchKey) {
        log.debug("Searching clients for user {} with key: {}", userId, searchKey);

        // Vérification de l'existence de l'utilisateur
        if (!appUserRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return clientRepository.findAllByNameContainingIgnoreCase(searchKey, pageable);
    }

    @Override
    public Client findClient(String userId, String id) {
        log.debug("Recovering client {} for user {}", id, userId);

        return clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
    }

    @Override
    @Transactional
    public boolean deleteClient(String userId, String id) {
        log.info("Initiation of client deletion for ID: {}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        if (client.getLicenses() == null || client.getLicenses().isEmpty()) {
            clientRepository.delete(client);

            log.info("Client {} deleted successfully", id);
            auditService.logAction("DELETE_CLIENT", userId, client.getName(), "SUCCESS");
            return true;
        } else {
            log.warn("Deletion failed: Client {} still has active licenses", id);
            auditService.logAction("DELETE_CLIENT", userId, client.getName(), "REJECTED - Has Licenses");
            return false;
        }
    }

    @Override
    @Transactional
    public Client updateClient(String userId, String id, Client clientData) {
        log.info("Updating client {} by user {}", id, userId);

        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        // Mise à jour des champs
        existingClient.setName(clientData.getName());
        existingClient.setEmail(clientData.getEmail());
        existingClient.setAddress(clientData.getAddress());
        existingClient.setPhone(clientData.getPhone());

        try {
            Client updated = clientRepository.save(existingClient);
            auditService.logAction("UPDATE_CLIENT", userId, updated.getName(), "SUCCESS");
            return updated;
        } catch (Exception e) {
            log.error("Update failed for client {}", id, e);
            auditService.logAction("UPDATE_CLIENT", userId, id, "FAILED");
            throw e;
        }
    }
}