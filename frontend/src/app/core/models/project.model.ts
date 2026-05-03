import {LicenseModel} from './license-model.model';

export interface Project  {
  id: string;

   name:string;

  description: string;

  createdAt: string;

  updatedAt:string;

  creatorName:string;

  licenseModel:LicenseModel;

}

// Interface pour le typage
export interface ProjectStats {
  total: number;
  totalThisMonth: number;
  newDeployments: number;
  lastDeployedName: string;
  topLicensedProject: string;
  leadArchitect: string;
}


