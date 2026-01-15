package com.taurustechnology.backend;

import com.taurustechnology.backend.services.impl.KeyGeneratorToolService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner initKeys(KeyGeneratorToolService keyService) {
        return args -> {
            try {
                keyService.checkAndGenerateKeys();
            } catch (Exception e) {
                System.err.println("❌ Erreur critique lors de l'initialisation des clés : " + e.getMessage());
                // Optionnel : System.exit(1); si les clés sont indispensables au démarrage
            }
        };
    }
}