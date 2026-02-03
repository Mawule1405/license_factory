import { Component } from '@angular/core';

@Component({
  selector: 'app-java-offline-docs',
  imports: [],
  templateUrl: './java-offline-docs.component.html',
  styleUrl: './java-offline-docs.component.css',
})
export class JavaOfflineDocsComponent {

  copyStatus: { [key: string]: string } = {};
  javaOfflineCode = `
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class TaurusOfflineProvider {
    // Intégrez ici votre clé publique fournie par Taurus Factory
    private static final String PUBLIC_KEY_PEM = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgK...";

    public static boolean verify(String data, String signatureB64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(PUBLIC_KEY_PEM);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey pubKey = kf.generatePublic(spec);

            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(pubKey);
            sig.update(data.getBytes());

            return sig.verify(Base64.getDecoder().decode(signatureB64));
        } catch (Exception e) {
            return false;
        }
    }
}`;

  javaOfflineUsage = `
// Exemple : lecture d'un fichier license.lic (format : data.signature)
String content = Files.readString(Path.of("license.lic"));
String[] parts = content.split("\\\\."); // On sépare les données de la signature

if (TaurusOfflineProvider.verify(parts[0], parts[1])) {
    System.out.println("Licence Authentique");
} else {
    System.err.println("ALERTE : Licence corrompue ou falsifiée !");
}`;

  copyCode(code: string, id: string): void {
    if (!code) return;

    navigator.clipboard.writeText(code).then(() => {
      // 1. On change le texte du bouton spécifique
      this.copyStatus[id] = 'Copié !';

      // 2. On remet le texte initial après 2 secondes
      setTimeout(() => {
        this.copyStatus[id] = 'Copier le code';
      }, 2000);
    }).catch(err => {
      console.error('Erreur lors de la copie : ', err);
      this.copyStatus[id] = 'Erreur';
    });
  }
}
