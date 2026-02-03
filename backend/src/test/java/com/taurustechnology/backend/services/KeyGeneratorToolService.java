package com.taurustechnology.backend.services;

import com.taurustechnology.backend.entities.License;
import com.taurustechnology.backend.services.impl.LicenseGeneratorServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class LicenseGeneratorServiceImplTest {

    private LicenseGeneratorServiceImpl licenseGeneratorService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Crée un dossier temporaire qui sera supprimé après le test
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Simuler la présence du fichier de clé dans le répertoire de travail
        // On génère une clé RSA 2048 de test
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();

        String encoded = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
        String pem = "-----BEGIN PRIVATE KEY-----\n" + encoded + "\n-----END PRIVATE KEY-----";

        // On crée le fichier attendu par le service à la racine (ou ici via un hack de chemin)
        // Note: Dans le service réel, PRIVATE_KEY_PATH = "private_key_pkcs8.key"
        Files.writeString(Path.of("private_key_pkcs8.key"), pem);

        licenseGeneratorService = new LicenseGeneratorServiceImpl(objectMapper);
    }

    @AfterEach
    void tearDown() throws Exception {
        // Nettoyage : on supprime le fichier de clé de test pour ne pas polluer le projet
        Files.deleteIfExists(Path.of("private_key_pkcs8.key"));
    }

    @Test
    @DisplayName("Devrait générer une licence signée valide (Payload + Signature)")
    void shouldGenerateValidSignedLicense() throws Exception {
        // Given
        License license = License.builder()
                .id("lic-123")
                .licenseKey("ABC-123")
                .maxUsers(10)
                .build();

        // When
        String result = licenseGeneratorService.buildLicense(license);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains(".");

        String[] parts = result.split("\\.");
        assertThat(parts).hasSize(2);

        // Vérifier que le payload est bien du Base64 (le JSON encodé)
        byte[] decodedPayload = Base64.getDecoder().decode(parts[0]);
        String json = new String(decodedPayload);
        assertThat(json).contains("ABC-123");

        // Vérifier que la signature est présente
        assertThat(parts[1]).isNotEmpty();
    }
}