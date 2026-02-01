package com.taurustechnology.backend.services;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

@Service
public class KeyGeneratorToolService {

    private static final String PRIVATE_KEY_PATH = "private_key_pkcs8.key";
    private static final String PUBLIC_KEY_PATH = "public_key.pub";

    public void checkAndGenerateKeys() throws Exception {
        File privFile = new File(PRIVATE_KEY_PATH);
        File pubFile = new File(PUBLIC_KEY_PATH);

        if (privFile.exists() && pubFile.exists()) {
            System.out.println("✅ Sécurité : Les clés RSA sont déjà présentes.");
            return;
        }

        System.out.println("⚠️ Sécurité : Clés introuvables. Génération du couple RSA en cours...");
        generate();
    }

    private void generate() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();

        saveToFile(PRIVATE_KEY_PATH, formatToPEM(
                Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded()), "PRIVATE KEY"));

        saveToFile(PUBLIC_KEY_PATH, formatToPEM(
                Base64.getEncoder().encodeToString(pair.getPublic().getEncoded()), "PUBLIC KEY"));

        System.out.println("🚀 Success : private_key_pkcs8.key et public_key.pub ont été créés.");
    }

    private String formatToPEM(String base64Key, String type) {
        return "-----BEGIN " + type + "-----\n" +
                base64Key.replaceAll("(.{64})", "$1\n") +
                "\n-----END " + type + "-----";
    }

    private void saveToFile(String fileName, String content) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
        }
    }
}