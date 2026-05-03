export interface Client {
  id?: string;
  name: string;
  email: string;
  address: string;
  phone: string;
  createdAt?: string;
  updatedAt?: string;
  registerName: string;

}


/**
 * Interface représentant les indicateurs de performance clés (KPI)
 * pour la gestion des clients dans l'écosystème Taurus.
 */
export interface ClientStats {
  // --- Volume & Croissance ---
  /** Nombre total de clients enregistrés en base de données */
  total: number;

  /** Nombre de nouveaux clients créés durant le mois en cours */
  totalThisMonth: number;

  /** Pourcentage de croissance par rapport au mois précédent */
  growthRate: number;

  // --- Activité Technique & Déploiement ---
  /** Nombre de licences actuellement valides (non expirées) sur le terrain */
  activeDeployments: number;

  /** Ratio moyen de projets déployés par client (Densité technique) */
  deploymentDensity: number;

  /** Taux de conversion : (Clients avec licence / Total clients) * 100 */
  conversionEfficiency: number;

  // --- Flux & Records ---
  /** Nom complet du dernier client ayant été enregistré ou mis à jour */
  lastDeployedName: string;

  /** Nom du projet le plus sollicité (ex: "NEBI", "VERBA") */
  topLicensedProject: string;

  // --- Ressources Humaines ---
  /** Nom de l'administrateur ou opérateur ayant le plus grand volume d'enregistrements */
  leadArchitect: string;
}
