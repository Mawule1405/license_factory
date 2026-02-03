import { Component } from '@angular/core';

@Component({
  selector: 'app-java-online-docs',
  imports: [],
  templateUrl: './java-online-docs.component.html',
  styleUrl: './java-online-docs.component.css',
})
export class JavaOnlineDocsComponent {
  copyStatus: { [key: string]: string } = {};

  providerCode = `
import java.net.URI;
import java.net.http.*;
import java.time.Instant;

public class TaurusLicenseProvider {
    private static boolean cachedResult = false;
    private static Instant lastCheck = Instant.MIN;
    private static final int TTL_HOURS = 4;

    /**
     * Accès rapide au résultat du cache (instantané).
     */
    public static boolean isAuthorized() {
        return cachedResult;
    }

    /**
     * Déclenche la validation si le cache est expiré.
     */
    public static void ensureValidation(String licenseKey) {
        if (Instant.now().isAfter(lastCheck.plusSeconds(TTL_HOURS * 3600))) {
            performValidation(licenseKey);
        }
    }

    private static void performValidation(String key) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.taurus.tech/verify"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\\"key\\":\\"" + key + "\\"}"))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                cachedResult = true;
                lastCheck = Instant.now();
            }
        } catch (Exception e) {
            // En cas d'erreur réseau, on conserve l'ancien état du cache (Grace Period)
        }
    }
}`;

  usageCode = `
// 1. Au démarrage de l'application (Main ou Initializer)
TaurusLicenseProvider.ensureValidation("VOTRE-CLE-ICI");

// 2. Dans vos fonctions sensibles
public void openSecureFeature() {
    if (TaurusLicenseProvider.isAuthorized()) {
        // Exécuter la fonctionnalité
    } else {
        throw new SecurityException("Licence invalide ou expirée.");
    }
}`;

  copyCode(code: string, id: string) {
    navigator.clipboard.writeText(code);
    this.copyStatus[id] = 'Copié !';
    setTimeout(() => this.copyStatus[id] = 'Copier le code', 2000);
  }
}
