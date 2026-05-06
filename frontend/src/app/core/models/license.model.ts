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

  parameters: LicenseParameter[];
}

export interface LicenseParameter{
  id: string;
  label: string;
  value: string;
  type: string;
}

export interface LicenseStats {
  // --- Volume ---
  total: number;             // Total des licences générées
  activeTotal: number;       // Licences actuellement actives (active: true)

  // --- Croissance & Performance ---
  growthRate: number;        // % de croissance vs mois précédent
  conversionEfficiency: number; // % de projets ayant au moins une licence active

  // --- Flux & Records ---
  lastDeployedName: string;  // Nom du client de la dernière licence générée
  topLicensedProject: string; // Nom du projet le plus licencié

  // --- Staff & Densité ---
  leadArchitect: string;     // Administrateur ayant généré le plus de licences
  deploymentDensity: number; // Moyenne de licences par projet
}

