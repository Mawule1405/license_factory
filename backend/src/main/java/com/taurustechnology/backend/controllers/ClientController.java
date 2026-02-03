package com.taurustechnology.backend.controllers;

import com.taurustechnology.backend.dtos.ClientDTO;

import com.taurustechnology.backend.entities.Client;
import com.taurustechnology.backend.mappers.ClientMapper;
import com.taurustechnology.backend.services.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper clientMapper;

    // 1. Créer un client
    @PostMapping("/{userId}")
    public ResponseEntity<ClientDTO> createClient(@PathVariable String userId, @RequestBody ClientDTO request) {
        Client client = clientMapper.toEntity(request);
        Client savedClient = clientService.save(userId, client);
        return ResponseEntity.ok(clientMapper.toDto(savedClient));
    }

    // 2. Lister les clients avec recherche et pagination
    @GetMapping("/{userId}")
    public ResponseEntity<Page<ClientDTO>> listClients(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String searchKey) {

        Page<Client> clients = clientService.findClients(userId, page, size, searchKey);
        return ResponseEntity.ok(clients.map(clientMapper::toDto));
    }

    // 3. Récupérer un client par son ID
    @GetMapping("/{userId}/{id}")
    public ResponseEntity<ClientDTO> getClient(@PathVariable String userId, @PathVariable String id) {
        Client client = clientService.findClient(userId, id);
        return ResponseEntity.ok(clientMapper.toDto(client));
    }

    // 4. Mettre à jour un client
    @PutMapping("/{userId}/{id}")
    public ResponseEntity<ClientDTO> updateClient(
            @PathVariable String userId,
            @PathVariable String id,
            @RequestBody ClientDTO request) {

        Client client = clientMapper.toEntity(request);
        Client updatedClient = clientService.updateClient(userId, id, client);
        return ResponseEntity.ok(clientMapper.toDto(updatedClient));
    }

    // 5. Supprimer un client
    @DeleteMapping("/{userId}/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable String userId, @PathVariable String id) {
        boolean deleted = clientService.deleteClient(userId, id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            // Retourne 400 Bad Request si le client a des licences actives (logique métier du service)
            return ResponseEntity.badRequest().build();
        }
    }
}