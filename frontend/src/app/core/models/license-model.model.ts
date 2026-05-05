export interface LicenseModel {
  id: string;
  projectId: string;

  parameters :Parameter[]
}

export interface Parameter{
  id: string;
  label: string;
  type: string;
}


