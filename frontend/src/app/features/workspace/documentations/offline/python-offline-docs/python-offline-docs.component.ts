import { Component } from '@angular/core';

@Component({
  selector: 'app-python-offline-docs',
  imports: [],
  templateUrl: './python-offline-docs.component.html',
  styleUrl: './python-offline-docs.component.css',
})
export class PythonOfflineDocsComponent {

  copyStatus: { [key: string]: string } = {};
  pythonOfflineCode = `
from Crypto.PublicKey import RSA
from Crypto.Signature import pkcs1_15
from Crypto.Hash import SHA256
import base64

class TaurusOfflineProvider:
    @staticmethod
    def verify_license(data, signature_b64, public_key_pem):
        """
        Vérifie si les données n'ont pas été modifiées
        et si elles ont bien été signées par Taurus.
        """
        try:
            key = RSA.import_key(public_key_pem)
            h = SHA256.new(data.encode('utf-8'))
            signature = base64.b64decode(signature_b64)

            pkcs1_15.new(key).verify(h, signature)
            return True # La signature est valide
        except (ValueError, TypeError):
            return False # Signature invalide ou données altérées
`;

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
