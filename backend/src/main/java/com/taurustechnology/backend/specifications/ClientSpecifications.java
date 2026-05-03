package com.taurustechnology.backend.specifications;

import com.taurustechnology.backend.models.Client;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ClientSpecifications {

    public static Specification<Client> createdBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> cb.between(root.get("createdAt"), start, end);
    }
}