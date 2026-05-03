package com.taurustechnology.backend.repositories;

import com.taurustechnology.backend.models.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, String> {
}
