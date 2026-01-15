package com.taurustechnology.backend.services.impl;


import com.taurustechnology.backend.dtos.LicenseRequest;
import com.taurustechnology.backend.services.LicenseGeneratorService;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;


import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Service
public class LicenseGeneratorServiceImpl implements LicenseGeneratorService {

    private final ObjectMapper objectMapper;
    private static final String PRIVATE_KEY_PATH = "private_key_pkcs8.key";

    public LicenseGeneratorServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Charge la clé privée depuis le fichier généré au démarrage.
     */
    private PrivateKey getPrivateKey() throws Exception {
        File keyFile = new File(PRIVATE_KEY_PATH);
        if (!keyFile.exists()) {
            throw new RuntimeException("Erreur : Le fichier de clé privée est introuvable à la racine.");
        }

        byte[] keyBytes = Files.readAllBytes(keyFile.toPath());
        String keyString = new String(keyBytes, StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decode = Base64.getDecoder().decode(keyString);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decode);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    /**
     * Génère la licence signée au format PAYLOAD.SIGNATURE
     */
    @Override
    public String buildLicense(LicenseRequest request) throws Exception {
        // 1. Transformer le DTO en JSON string
        String json = objectMapper.writeValueAsString(request);

        // 2. Encodage Base64 du JSON (pour garantir l'intégrité du texte)
        String encodedPayload = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));

        // 3. Signature numérique avec SHA256withRSA
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(getPrivateKey());
        sig.update(encodedPayload.getBytes(StandardCharsets.UTF_8));

        byte[] signatureBytes = sig.sign();
        String encodedSignature = Base64.getEncoder().encodeToString(signatureBytes);

        // 4. Format final concaténé
        return encodedPayload + "." + encodedSignature;
    }
}