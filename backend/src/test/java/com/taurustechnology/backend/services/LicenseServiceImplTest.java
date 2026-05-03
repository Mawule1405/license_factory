package com.taurustechnology.backend.services;

import com.taurustechnology.backend.models.AppUser;
import com.taurustechnology.backend.models.Client;
import com.taurustechnology.backend.models.License;
import com.taurustechnology.backend.repositories.AppUserRepository;
import com.taurustechnology.backend.repositories.ClientRepository;
import com.taurustechnology.backend.repositories.LicenseRepository;
import com.taurustechnology.backend.services.impl.LicenseServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LicenseServiceImplTest {

    @Mock private LicenseRepository licenseRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private AppUserRepository appUserRepository;
    @Mock private AuditService auditService;
    @Mock private LicenseGeneratorService licenseGeneratorService;

    @InjectMocks
    private LicenseServiceImpl licenseService;

    private AppUser mockUser;
    private Client mockClient;
    private License mockLicense;


}