package com.taurustechnology.backend.services;

import com.taurustechnology.backend.models.AppUser;
import com.taurustechnology.backend.models.Client;
import com.taurustechnology.backend.models.License;
import com.taurustechnology.backend.repositories.AppUserRepository;
import com.taurustechnology.backend.repositories.ClientRepository;
import com.taurustechnology.backend.services.impl.ClientServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private ClientServiceImpl clientService;

    private AppUser mockUser;
    private Client mockClient;

    @BeforeEach
    void setUp() {
        mockUser = AppUser.builder().id("user-1").username("admin").build();
        mockClient = Client.builder()
                .id("client-1")
                .name("Taurus Tech")
                .licenses(new ArrayList<>())
                .build();
    }

    @Nested
    @DisplayName("Tests de sauvegarde (save)")
    class SaveTests {

        @Test
        @DisplayName("Devrait sauvegarder le client et logger l'audit en cas de succès")
        void shouldSaveClientSuccessfully() {
            // Given
            when(appUserRepository.findById("user-1")).thenReturn(Optional.of(mockUser));
            when(clientRepository.save(any(Client.class))).thenReturn(mockClient);

            // When
            Client saved = clientService.save("user-1", mockClient);

            // Then
            assertThat(saved).isNotNull();
            assertThat(saved.getCreator()).isEqualTo(mockUser);
            verify(auditService).logAction(eq("CREATE_CLIENT"), eq("user-1"), anyString(), eq("SUCCESS"));
            verify(clientRepository).save(mockClient);
        }

        @Test
        @DisplayName("Devrait lancer une exception si l'utilisateur n'existe pas")
        void shouldThrowExceptionWhenUserNotFound() {
            when(appUserRepository.findById("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clientService.save("unknown", mockClient))
                    .isInstanceOf(EntityNotFoundException.class);

            verify(clientRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Tests de suppression (deleteClient)")
    class DeleteTests {

        @Test
        @DisplayName("Succès : Supprime le client s'il n'a pas de licences")
        void shouldDeleteClientWhenNoLicenses() {
            // Given
            when(clientRepository.findById("client-1")).thenReturn(Optional.of(mockClient));

            // When
            boolean result = clientService.deleteClient("user-1", "client-1");

            // Then
            assertThat(result).isTrue();
            verify(clientRepository).delete(mockClient);
            verify(auditService).logAction(any(), any(), any(), eq("SUCCESS"));
        }

        @Test
        @DisplayName("Echec : Refuse la suppression si des licences sont présentes")
        void shouldReturnFalseWhenClientHasLicenses() {
            // Given
            mockClient.setLicenses(List.of(new License())); // On ajoute une licence
            when(clientRepository.findById("client-1")).thenReturn(Optional.of(mockClient));

            // When
            boolean result = clientService.deleteClient("user-1", "client-1");

            // Then
            assertThat(result).isFalse();
            verify(clientRepository, never()).delete(any());
            verify(auditService).logAction(eq("DELETE_CLIENT"), any(), any(), contains("REJECTED"));
        }
    }

    @Nested
    @DisplayName("Tests de mise à jour (updateClient)")
    class UpdateTests {

        @Test
        @DisplayName("Devrait mettre à jour les champs autorisés")
        void shouldUpdateAllowedFields() {
            // Given
            Client updatedData = Client.builder()
                    .name("New Name")
                    .email("new@test.com")
                    .build();

            when(clientRepository.findById("client-1")).thenReturn(Optional.of(mockClient));
            when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            Client result = clientService.updateClient("user-1", "client-1", updatedData);

            // Then
            assertThat(result.getName()).isEqualTo("New Name");
            assertThat(result.getEmail()).isEqualTo("new@test.com");
            verify(auditService).logAction(eq("UPDATE_CLIENT"), eq("user-1"), eq("New Name"), eq("SUCCESS"));
        }
    }
}