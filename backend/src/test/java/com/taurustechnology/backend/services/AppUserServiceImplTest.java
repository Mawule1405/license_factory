package com.taurustechnology.backend.services;

import com.taurustechnology.backend.models.AppUser;
import com.taurustechnology.backend.repositories.AppUserRepository;
import com.taurustechnology.backend.services.impl.AppUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceImplTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AppUserServiceImpl appUserService;

    private AppUser creator;
    private AppUser newUser;

    @BeforeEach
    void setUp() {
        creator = AppUser.builder().id("creator-id").username("admin").build();
        newUser = AppUser.builder()
                .username("jdoe")
                .email("jdoe@test.com")
                .passwordHash("raw-password")
                .build();
    }

    @Nested
    @DisplayName("Tests de la méthode create")
    class CreateUserTests {

        @Test
        @DisplayName("Succès : création d'un utilisateur avec encodage du mot de passe")
        void shouldCreateUserSuccessfully() {
            // Given
            when(appUserRepository.findById("creator-id")).thenReturn(Optional.of(creator));
            when(appUserRepository.existsByUsername("jdoe")).thenReturn(false);
            when(appUserRepository.existsByEmail("jdoe@test.com")).thenReturn(false);
            when(passwordEncoder.encode("raw-password")).thenReturn("encoded-password");
            when(appUserRepository.save(any(AppUser.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            AppUser created = appUserService.create(newUser, "creator-id");

            // Then
            assertThat(created).isNotNull();
            assertThat(created.getPasswordHash()).isEqualTo("encoded-password");
            assertThat(created.isActivated()).isTrue();
            verify(appUserRepository).save(any(AppUser.class));
        }

        @Test
        @DisplayName("Erreur : exception si le username existe déjà")
        void shouldThrowExceptionWhenUsernameExists() {
            // Given
            when(appUserRepository.findById("creator-id")).thenReturn(Optional.of(creator));
            when(appUserRepository.existsByUsername("jdoe")).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> appUserService.create(newUser, "creator-id"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Username already exists");
        }
    }

    @Nested
    @DisplayName("Tests de la gestion de session (Login/Logout)")
    class SessionTests {

        @Test
        @DisplayName("Login : devrait passer loggedIn à true")
        void shouldSetLoggedInToTrueOnLogin() {
            // Given
            String userId = "user-123";
            AppUser user = AppUser.builder().id(userId).loggedIn(false).build();
            when(appUserRepository.findById(userId)).thenReturn(Optional.of(user));
            when(appUserRepository.save(any(AppUser.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            AppUser result = appUserService.login(userId);

            // Then
            assertThat(result.isLoggedIn()).isTrue();
            assertThat(result.getUpdatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Tests de suppression (Soft Delete)")
    class DeleteTests {

        @Test
        @DisplayName("Delete : devrait marquer l'utilisateur comme supprimé (soft delete)")
        void shouldPerformSoftDelete() {
            // Given
            String idToDelete = "target-id";
            AppUser target = AppUser.builder().id(idToDelete).deleted(false).build();
            when(appUserRepository.findById(idToDelete)).thenReturn(Optional.of(target));

            // When
            boolean result = appUserService.deleteById(idToDelete, "admin-id");

            // Then
            assertThat(result).isTrue();
            assertThat(target.isDeleted()).isTrue();
            verify(appUserRepository).save(target);
        }
    }
}