import {ChangeDetectorRef, Component, inject, OnInit} from '@angular/core';
import {ClientService} from '../../../../core/services/client.service';
import {Client} from '../../../../core/models/client.model';
import {FormsModule} from '@angular/forms';
import {RouterLink} from '@angular/router';
import {CreateClientModalComponent} from './create-client-modal/create-client-modal.component';
import {PaginationComponent} from '../../../../shared/components/layout/pagination/pagination.component';
import {DatePipe} from '@angular/common';
import {EditClientModalComponent} from './edit-client-modal/edit-client-modal.component';

@Component({
  selector: 'app-list-clients.component',
  imports: [
    FormsModule,
    RouterLink,
    CreateClientModalComponent,
    PaginationComponent,
    DatePipe,
    EditClientModalComponent
  ],
  templateUrl: './list-clients.component.html',
  styleUrl: './list-clients.component.css',
})
export class ListClientsComponent implements OnInit{
  private clientService = inject(ClientService);
  private cdr = inject(ChangeDetectorRef);

  clients: Client[] = [];
  loading = false;
  searchKey = '';


  isModalOpen = false;
  showDeleteConfirm = false
  pagination = {
    totalPages: 0,
    totalElements: 0,
    page:1,
    size:10,
  };
  isEditModalOpen = false
  selectedClient?: Client;

  ngOnInit() {
    this.loadClients();
  }

  loadClients() {
    this.loading = true;
    this.clientService.fetchClients(this.pagination.page, this.pagination.size, this.searchKey)
      .subscribe({
        next: (res) => {
          this.loading = false;
          this.clients = res.content;
          this.pagination.totalElements = res.totalElements;
          this.pagination.totalPages = res.totalPages;
          this.pagination.page = res.page;
          this.pagination.size = res.size;
          this.cdr.detectChanges();
        },
        error: () => {
          this.loading = false
          this.cdr.detectChanges();
        }
      });
  }

  onSearch() {
    this.pagination.page = 1;
    this.loadClients();
  }

  handleDelete(id: string) {
    this.showDeleteConfirm = true;
  }

  deleteClient() {

  }

  handlePageChange(page: number) {
    this.pagination.page = page;
    this.loadClients();
  }

  handleSizeChange(size: number) {
    this.pagination.size = size;
    this.pagination.page = 1; // Toujours revenir à la page 1
    this.loadClients();
  }

  editClient(client: Client) {
    this.selectedClient = client;
    this.isEditModalOpen = true;
  }
}
