export interface AppRole {
  id?: number;
  name: string;
  description?: string;
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
}

// Interface pour la réponse paginée de Spring Data
export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}
