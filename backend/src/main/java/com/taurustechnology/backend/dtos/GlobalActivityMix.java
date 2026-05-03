package com.taurustechnology.backend.dtos;

// DTO pour le mix opérationnel global
public record GlobalActivityMix(
        long licensing,
        long registrations,
        long exports,
        long admin
) {}
