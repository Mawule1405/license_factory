package com.taurustechnology.backend.services.impl;


import com.taurustechnology.backend.dtos.requests.LicenseRequest;
import com.taurustechnology.backend.models.License;
import com.taurustechnology.backend.models.LicenseParameter;
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
import java.util.HashMap;
import java.util.Map;

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


    @Override
    public String buildLicense(License request) throws Exception {
        // 1. Préparation du contenu (Payload)
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("licenseId", request.getId());
        payloadMap.put("clientId", request.getClient().getId());
        payloadMap.put("projectName", request.getProject().getName());
        payloadMap.put("activationCode", request.getActivationCode());
        // Conversion de la List<LicenseParameter> en Map pour le JSON final
        Map<String, String> dynamicParams = new HashMap<>();
        if (request.getParameters() != null) {
            for (LicenseParameter param : request.getParameters()) {
                dynamicParams.put(param.getLabel(), param.getValue());
            }
        }
        payloadMap.put("parameters", dynamicParams);
        payloadMap.put("createdAt", java.time.LocalDateTime.now().toString());

        // Sérialisation en JSON
        String json = objectMapper.writeValueAsString(payloadMap);

        // 2. Encodage Base64
        String encodedPayload = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));

        // 3. Signature SHA256withRSA
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(getPrivateKey());
        sig.update(encodedPayload.getBytes(StandardCharsets.UTF_8));

        byte[] signatureBytes = sig.sign();
        String encodedSignature = Base64.getEncoder().encodeToString(signatureBytes);

        // 4. Format Final : Payload.Signature
        return encodedPayload + "." + encodedSignature;
    }
}