import {ChangeDetectorRef, Component, inject, OnInit} from '@angular/core';
import {ClientService} from '../../../../core/services/client.service';
import {Client} from '../../../../core/models/client.model';
import {FormsModule} from '@angular/forms';
import {RouterLink} from '@angular/router';
import {CreateClientModalComponent} from './create-client-modal/create-client-modal.component';

@Component({
  selector: 'app-list-clients.component',
  imports: [
    FormsModule,
    RouterLink,
    CreateClientModalComponent
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
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;
  isModalOpen = false;
  showDeleteConfirm = false

  ngOnInit() {
    this.loadClients();
  }

  loadClients() {
    this.loading = true;
    this.clientService.findClients(this.currentPage, this.pageSize, this.searchKey)
      .subscribe({
        next: (res) => {
          this.loading = false;
          this.clients = res.content;
          this.totalElements = res.totalElements;
          this.totalPages = res.totalPages;
          this.cdr.detectChanges();
        },
        error: () => {
          this.loading = false
          this.cdr.detectChanges();
        }
      });
  }

  onSearch() {
    this.currentPage = 0;
    this.loadClients();
  }

  handleDelete(id: string) {
    this.showDeleteConfirm = true;
  }

  deleteClient() {

  }
}
