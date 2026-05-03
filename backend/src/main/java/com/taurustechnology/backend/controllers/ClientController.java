package com.taurustechnology.backend.controllers;

import com.taurustechnology.backend.dtos.ClientDTO;
import com.taurustechnology.backend.dtos.responses.ClientResponse;
import com.taurustechnology.backend.dtos.responses.Pagination;
import com.taurustechnology.backend.models.Client;
import com.taurustechnology.backend.mappers.ClientMapper;
import com.taurustechnology.backend.services.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper clientMapper;




    // 1. Créer un client
    @PostMapping
    public ResponseEntity<ClientResponse> createClient(Principal principal, @RequestBody ClientDTO request) {
        log.info("REST request to create client by user: {}", principal.getName());
        Client client = clientMapper.toEntity(request);
        Client savedClient = clientService.save(principal.getName(), client);
        return ResponseEntity.ok(clientMapper.toDto(savedClient));
    }

    // 2. Lister les clients avec recherche et pagination
    @GetMapping
    public ResponseEntity<Pagination<ClientResponse>> listClients(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String searchKey) {

        log.info("REST request to list clients for user: {}", principal.getName());
        Page<Client> clients = clientService.findClients(principal.getName(), page, size, searchKey);
        return ResponseEntity.ok(Pagination.of(clients.map(clientMapper::toDto)));
    }

    // 3. Récupérer un client par son ID
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getClient(Principal principal, @PathVariable String id) {
        log.info("REST request to get client ID: {} by user: {}", id, principal.getName());
        Client client = clientService.findClient(principal.getName(), id);
        return ResponseEntity.ok(clientMapper.toDto(client));
    }

    // 4. Mettre à jour un client
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> updateClient(
            Principal principal,
            @PathVariable String id,
            @RequestBody ClientDTO request) {

        log.info("REST request to update client ID: {} by user: {}", id, principal.getName());
        Client client = clientMapper.toEntity(request);
        Client updatedClient = clientService.updateClient(principal.getName(), id, client);
        return ResponseEntity.ok(clientMapper.toDto(updatedClient));
    }

    // 5. Supprimer un client
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(Principal principal, @PathVariable String id) {
        log.warn("REST request to delete client ID: {} by user: {}", id, principal.getName());
        boolean deleted = clientService.deleteClient(principal.getName(), id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}