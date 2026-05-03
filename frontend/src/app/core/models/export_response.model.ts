export interface ExportResponse {
  id: string;

  // Informations sur l'administrateur (le "register")
  adminId: string;
  adminFullName: string;

  // Informations sur la licence
  licenseId: string;
  activationCode: string;
  clientName: string;
  projectName: string;

  // Détails de l'exportation
  details: string; // Contient la raison de l'exportation
  createdAt: string | Date; // Reçu en format ISO string de l'API
}
