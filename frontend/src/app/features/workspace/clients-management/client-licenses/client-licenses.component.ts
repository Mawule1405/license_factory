import {ChangeDetectorRef, Component, inject, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Client } from '../../../../core/models/client.model';
import { LicenseDTO } from '../../../../core/models/license.model';
import { ClientService } from '../../../../core/services/client.service';
import { LicenseService } from '../../../../core/services/license.service';
import { ClientUiService } from '../../../../core/services/client-ui.service';
import {CreateClientLicenceModalComponent} from './create-client-licence-modal/create-client-licence-modal.component';

@Component({
  selector: 'app-client-licenses',
  standalone: true,
  imports: [CommonModule, CreateClientLicenceModalComponent],
  templateUrl: './client-licenses.component.html'
})
export class ClientLicensesComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private clientService = inject(ClientService);
  private licenseService = inject(LicenseService);
  private uiService = inject(ClientUiService);
  private cdr = inject(ChangeDetectorRef);

  // Data & Context
  client?: Client;
  licenses: LicenseDTO[] = [];
  clientId!: string;

  // State
  loading = false;

  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;

  showLicenseCreationModal = false;

  ngOnInit() {
    this.clientId = this.route.snapshot.params['clientId'];
    if (this.clientId) {
      this.initView();
    }
  }

  /** Initialise la vue : Récupère le client ET les licences */
  initView() {
    this.loading = true;
    this.clientService.getClientById( this.clientId).subscribe({
      next: (client) => {
        this.client = client;
        this.uiService.setClientName(client.name); // Mise à jour du Breadcrumb
        this.loadLicenses();
      },
      error: () => {
        this.loading = false
        this.cdr.detectChanges()
      }
    });
  }

  /** Charge la liste paginée des licences */
  loadLicenses() {
    this.loading = true;
    this.licenseService.getLicenses(this.currentPage, this.pageSize)
      .subscribe({
        next: (res) => {
          // Note : On filtre côté client si le backend n'a pas de endpoint filtré
          this.licenses = res.content.filter(l => l.clientId === this.clientId);
          this.totalElements = res.totalElements;
          this.totalPages = res.totalPages;
          this.loading = false;
          this.cdr.detectChanges();
        },
        error: () => {
          this.loading = false
          this.cdr.detectChanges()
        }
      });
  }

  /** Navigation de page */
  goToPage(delta: number) {
    let page = this.currentPage + delta;
    if (page>=0 && page <= this.totalPages) {
      this.currentPage = page;
      this.loadLicenses();
    }

  }

  /** Action de téléchargement du fichier .lic */
  download(license: LicenseDTO) {
    if (!license.id) return;
    this.licenseService.downloadLicenseFile( license.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `TAURUS_${this.client?.name}_${license.level}.lic`.replace(/\s+/g, '_');
        a.click();
        window.URL.revokeObjectURL(url);
      }
    });
  }

  createNewLicense() {
    this.showLicenseCreationModal = true
  }

  onCreateLicense($event: void) {
    this.showLicenseCreationModal = false
    this.initView()
  }
}
