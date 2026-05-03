export interface LicenseResponse {
  id: string;

  // Informations de base
  activationCode: string;
  active: boolean;
  createdAt: Date | string; // Date si parsé, string si brut (ISO 8601)

  // Détails du Client
  clientId: string;
  clientName: string;
  clientEmail: string;

  // Détails du Projet
  projectId: string;
  projectName: string;

  // Utilisateur ayant généré la licence
  creatorName: string;

  parameters: { [key: string]: string };
}
