package com.taurustechnology.backend.services;

import com.taurustechnology.backend.entities.AppUser;
import com.taurustechnology.backend.entities.Client;
import com.taurustechnology.backend.entities.License;
import com.taurustechnology.backend.repositories.AppUserRepository;
import com.taurustechnology.backend.repositories.ClientRepository;
import com.taurustechnology.backend.repositories.LicenseRepository;
import com.taurustechnology.backend.services.AuditService;
import com.taurustechnology.backend.services.LicenseGeneratorService;
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

    @BeforeEach
    void setUp() {
        mockUser = AppUser.builder().id("user-1").username("admin").build();
        mockClient = Client.builder().id("client-1").name("Taurus Client").build();
        mockLicense = License.builder()
                .id("lic-123")
                .client(mockClient)
                .licenseKey(null) // Pour tester la génération auto
                .expiryDate(LocalDateTime.now().plusDays(30))
                .activated(true)
                .deleted(false)
                .build();
    }

    @Nested
    @DisplayName("Tests de sauvegarde (save)")
    class SaveTests {

        @Test
        @DisplayName("Succès : Création avec génération automatique de LicenseKey")
        void shouldSaveLicenseWithGeneratedKey() {
            // Given
            when(appUserRepository.findById("user-1")).thenReturn(Optional.of(mockUser));
            when(clientRepository.existsById("client-1")).thenReturn(true);
            when(licenseRepository.save(any(License.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            License saved = licenseService.save("user-1", mockLicense);

            // Then
            assertThat(saved.getLicenseKey()).isNotNull();
            assertThat(saved.getLicenseKey()).hasSizeGreaterThan(10);
            assertThat(saved.getCreator()).isEqualTo(mockUser);
            verify(auditService).logAction(eq("CREATE_LICENSE"), eq("user-1"), anyString(), eq("SUCCESS"));
        }

        @Test
        @DisplayName("Erreur : Exception si le client associé n'existe pas")
        void shouldThrowExceptionWhenClientNotFound() {
            when(appUserRepository.findById("user-1")).thenReturn(Optional.of(mockUser));
            when(clientRepository.existsById("client-1")).thenReturn(false);

            assertThatThrownBy(() -> licenseService.save("user-1", mockLicense))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Client associated with license not found");
        }
    }

    @Nested
    @DisplayName("Tests de génération (generateLicense)")
    class GenerateTests {

        @Test
        @DisplayName("Succès : Génération de la chaîne signée")
        void shouldGenerateSignedLicenseString() throws Exception {
            // Given
            String expectedSignature = "SIGNED_CONTENT_EXAMPLE";
            when(appUserRepository.findById("user-1")).thenReturn(Optional.of(mockUser));
            when(licenseRepository.findById("lic-123")).thenReturn(Optional.of(mockLicense));
            when(licenseGeneratorService.buildLicense(mockLicense)).thenReturn(expectedSignature);

            // When
            String result = licenseService.generateLicense("user-1", "lic-123");

            // Then
            assertThat(result).isEqualTo(expectedSignature);
            verify(auditService).logAction(eq("GENERATE_SIGNED_KEY"), eq("user-1"), any(), eq("SUCCESS"));
        }

        @Test
        @DisplayName("Erreur : Exception si la licence est expirée")
        void shouldThrowExceptionWhenLicenseExpired() {
            // Given
            mockLicense.setExpiryDate(LocalDateTime.now().minusDays(1)); // Expire hier
            when(appUserRepository.findById("user-1")).thenReturn(Optional.of(mockUser));
            when(licenseRepository.findById("lic-123")).thenReturn(Optional.of(mockLicense));

            // When & Then
            assertThatThrownBy(() -> licenseService.generateLicense("user-1", "lic-123"))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("Tests de suppression (delete)")
    class DeleteTests {

        @Test
        @DisplayName("Soft Delete : Désactive la licence au lieu de la supprimer")
        void shouldPerformSoftDelete() {
            // Given
            when(licenseRepository.findById("lic-123")).thenReturn(Optional.of(mockLicense));

            // When
            boolean result = licenseService.delete("user-1", "lic-123");

            // Then
            assertThat(result).isTrue();
            assertThat(mockLicense.isDeleted()).isTrue();
            assertThat(mockLicense.isActivated()).isFalse();
            verify(licenseRepository).save(mockLicense);
            verify(auditService).logAction(eq("DELETE_LICENSE"), eq("user-1"), any(), eq("SUCCESS"));
        }
    }
}