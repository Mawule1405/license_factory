package com.taurustechnology.backend.services;

import com.taurustechnology.backend.models.AppRole;
import com.taurustechnology.backend.repositories.AppRoleRepository;
import com.taurustechnology.backend.services.impl.AppRoleServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppRoleServiceImplTest {

    @Mock
    private AppRoleRepository appRoleRepository;

    @InjectMocks
    private AppRoleServiceImpl appRoleService;

    @Nested
    @DisplayName("Tests pour la méthode save")
    class SaveTests {

        @Test
        @DisplayName("Devrait sauvegarder un rôle avec succès")
        void shouldSaveRoleSuccessfully() {
            // Given
            AppRole inputRole = AppRole.builder().name("ADMIN").build();
            AppRole savedRole = AppRole.builder().id("uuid-123").name("ADMIN").build();

            when(appRoleRepository.save(any(AppRole.class))).thenReturn(savedRole);

            // When
            AppRole result = appRoleService.save(inputRole);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("uuid-123");
            verify(appRoleRepository, times(1)).save(inputRole);
        }

        @Test
        @DisplayName("Devrait lancer une exception si le rôle est null")
        void shouldThrowExceptionWhenRoleIsNull() {
            assertThatThrownBy(() -> appRoleService.save(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("AppRole cannot be null");
        }
    }

    @Nested
    @DisplayName("Tests pour la méthode findById")
    class FindByIdTests {

        @Test
        @DisplayName("Devrait retourner un rôle s'il existe")
        void shouldReturnRoleIfExists() {
            // Given
            String id = "1";
            AppRole role = AppRole.builder().id("1").name("USER").build();
            when(appRoleRepository.findById(id)).thenReturn(Optional.of(role));

            // When
            Optional<AppRole> result = appRoleService.findById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("USER");
        }

        @Test
        @DisplayName("Devrait lancer une exception si l'ID est vide ou null")
        void shouldThrowExceptionWhenIdIsInvalid() {
            assertThatThrownBy(() -> appRoleService.findById("  "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Role ID cannot be null or empty");
        }
    }

    @Nested
    @DisplayName("Tests pour la méthode existsByName")
    class ExistsByNameTests {

        @Test
        @DisplayName("Devrait retourner true si le nom existe")
        void shouldReturnTrueIfNameExists() {
            // Given
            String roleName = "ADMIN";
            when(appRoleRepository.existsByName(roleName)).thenReturn(true);

            // When
            boolean exists = appRoleService.existsByName(roleName);

            // Then
            assertThat(exists).isTrue();
            verify(appRoleRepository).existsByName(roleName);
        }
    }

    @Test
    @DisplayName("findAll devrait retourner la liste complète")
    void findAllShouldReturnList() {
        // Given
        List<AppRole> roles = List.of(
                AppRole.builder().id("1").name("R1").build(),
                AppRole.builder().id("2").name("R2").build());
        when(appRoleRepository.findAll()).thenReturn(roles);

        // When
        List<AppRole> result = appRoleService.findAll();

        // Then
        assertThat(result).hasSize(2);
        verify(appRoleRepository).findAll();
    }
}