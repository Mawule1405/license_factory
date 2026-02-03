import { Component } from '@angular/core';

@Component({
  selector: 'app-python-online-docs',
  imports: [],
  templateUrl: './python-online-docs.component.html',
  styleUrl: './python-online-docs.component.css',
})
export class PythonOnlineDocsComponent {
  copyStatus: { [key: string]: string } = {};

  providerCode = `
import requests
import time

class TaurusLicenseProvider:
    _cached_result = False
    _last_check_timestamp = 0
    _ttl_seconds = 14400  # 4 Heures

    @classmethod
    def is_authorized(cls):
        """ Retourne l'état actuel de la licence en mémoire. """
        return cls._cached_result

    @classmethod
    def validate_if_needed(cls, license_key):
        """
        Vérifie si une validation est nécessaire.
        Si le cache est expiré, effectue une requête vers Taurus API.
        """
        now = time.time()
        if not cls._cached_result or (now - cls._last_check_timestamp > cls._ttl_seconds):
            cls._perform_validation(license_key)

    @classmethod
    def _perform_validation(cls, key):
        try:
            url = "https://api.taurus.tech/verify"
            response = requests.post(
                url,
                json={"key": key},
                timeout=5
            )

            if response.status_code == 200:
                cls._cached_result = True
                cls._last_check_timestamp = time.time()
            else:
                cls._cached_result = False
        except Exception:
            # En cas d'erreur réseau, on garde le dernier état connu (Grace Period)
            pass
`;

  usageCode = `
# 1. Initialisation au lancement du script
TaurusLicenseProvider.validate_if_needed("KEY-XXXX-YYYY")

# 2. Utilisation dans le flux logique
def restricted_action():
    if TaurusLicenseProvider.is_authorized():
        print("Accès accordé à la fonctionnalité Taurus")
    else:
        print("Erreur : Licence non valide")
`;

  copyCode(code: string, id: string) {
    navigator.clipboard.writeText(code);
    this.copyStatus[id] = 'Copié !';
    setTimeout(() => this.copyStatus[id] = 'Copier le code', 2000);
  }
}
