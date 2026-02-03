package com.taurustechnology.backend.services;

public interface AuditService {
    void logAction(String action, String user, String target, String status);
}
