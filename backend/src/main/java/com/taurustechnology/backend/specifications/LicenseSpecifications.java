package com.taurustechnology.backend.specifications;

import com.taurustechnology.backend.models.License;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class LicenseSpecifications {
    public static Specification<License> createdBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> cb.between(root.get("createdAt"), start, end);
    }
}