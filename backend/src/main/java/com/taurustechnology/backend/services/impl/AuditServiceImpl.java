package com.taurustechnology.backend.services.impl;


import com.taurustechnology.backend.services.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditServiceImpl implements AuditService {
    // On récupère spécifiquement le logger "AUDIT" défini dans le XML
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");

    @Override
    public void logAction(String action, String user, String target, String status) {
        // Format : ACTION | USER | TARGET | STATUS
        String message = String.format("%-15s | %-15s | %-20s | %s",
                action, user, target, status);
        auditLogger.info(message);
    }
}