export interface AppRole {

  id?: number;
  name: string;
  description?: string;
  userCount?: string;
}

export interface AppUser {
  id: string;
  username: string;
  email: string;
  fullName: string;
  activated: boolean;
  deleted: boolean;
  loggedIn: boolean;
  createdAt: Date;
  updatedAt: Date;
  appRoles: AppRole[];
  licenseCount: number;
  exportCount: number;
  clientCount: number;
  projectCount: number;
}

// Interface pour la réponse paginée de Spring Data
export interface Pagination<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  page: number;
}


