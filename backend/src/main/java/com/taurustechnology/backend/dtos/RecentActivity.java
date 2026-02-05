package com.taurustechnology.backend.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecentActivity {
    private String timestamp; // Ex: "2026-02-04 19:30"
    private String action;    // Ex: "LICENSE_GENERATED"
    private String details;   // Ex: "Client: Microsoft | User: admin"
    private String status;    // Ex: "SUCCESS", "WARN", "DANGER"
}