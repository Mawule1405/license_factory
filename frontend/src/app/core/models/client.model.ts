export interface Client {
  id?: string;
  name: string;
  email: string;
  address: string;
  phone: string;
  createdAt?: string;
  creatorId?: string;
  numberOfLicenses?: number;
}
