export enum LicenseLevel {
  FREEMIUM = 'FREEMIUM',
  BASIC = 'BASIC',
  CLASSIC = 'CLASSIC',
  COMMUNITY= 'COMMUNITY',
  STANDARD = 'STANDARD',
  PREMIUM = 'PREMIUM',
  ENTERPRISE = 'ENTERPRISE',
  

}

export interface LicenseDTO {
  id?: string;
  licenseKey?: string;
  addressMac: string;
  level: LicenseLevel;
  maxUsers: number;
  createdAt?: Date;
  expiryDate: string; // Utilisation de string pour l'input date HTML
  activated: boolean;
  deleted: boolean;
  clientId: string;
  creatorId?: string;
}

