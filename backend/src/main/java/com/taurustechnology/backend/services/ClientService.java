package com.taurustechnology.backend.services;

import com.taurustechnology.backend.models.Client;
import org.springframework.data.domain.Page;

public interface ClientService {

    Client save(String userId, Client client);
    Page<Client> findClients(String userId, int page, int size, String searchKey);
    Client findClient(String userId, String id);
    boolean deleteClient(String userId, String id);
    Client updateClient(String userId, String id, Client client);

}
