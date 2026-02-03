package com.taurustechnology.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Dans tes DTOs
public record RefreshTokenRequest(String refreshToken) {}
