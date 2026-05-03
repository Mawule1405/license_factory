import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ActivatedRoute, RouterLink} from '@angular/router';
import { Client } from '../../../../core/models/client.model';
import { LicenseResponse } from '../../../../core/models/license.model';
import { ClientService } from '../../../../core/services/client.service';
import { LicenseService } from '../../../../core/services/license.service';
import { ClientUiService } from '../../../../core/services/client-ui.service';
import { CreateClientLicenceModalComponent } from './create-client-licence-modal/create-client-licence-modal.component';
import {PaginationComponent} from '../../../../shared/components/layout/pagination/pagination.component';
import {
  ExportLicenseRaisonModalComponent
} from '../../../../shared/components/modals/export-license-raison-modal/export-license-raison-modal.component';
import {NotificationService} from '../../../../core/services/notification.service';

@Component({
  selector: 'app-client-licenses',
  standalone: true,
  imports: [CommonModule, CreateClientLicenceModalComponent, PaginationComponent, RouterLink, ExportLicenseRaisonModalComponent],
  templateUrl: './client-licenses.component.html'
})
export class ClientLicensesComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private clientService = inject(ClientService);
  private licenseService = inject(LicenseService);
  private uiService = inject(ClientUiService);
  private notifyService = inject(NotificationService);
  private cdr = inject(ChangeDetectorRef);

  // Data
  client?: Client;
  licenses: LicenseResponse[] = [];
  clientId!: string;

  // State
  loading = false;
  showLicenseCreationModal = false;
  isExportLicenseModal= false
  selectedLicense? : LicenseResponse

  // Pagination conforme à ton composant
  pagination = {
    page: 1,
    size: 10,
    totalElements: 0,
    totalPages: 0
  };

  ngOnInit() {
    this.clientId = this.route.snapshot.params['clientId'];
    if (this.clientId) {
      this.initView();
    }
  }

  initView() {
    this.loading = true;
    this.clientService.getClientById(this.clientId).subscribe({
      next: (client) => {
        this.client = client;
        this.uiService.setClientName(client.name);
        this.loadLicenses();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadLicenses() {
    this.loading = true;
    // Utilisation des paramètres de pagination
    this.licenseService.getClientLicenses(this.clientId, this.pagination.page, this.pagination.size)
      .subscribe({
        next: (res) => {
          // On garde res.content directement (le backend doit idéalement filtrer par clientId)
          this.licenses = res.content;
          this.pagination.totalElements = res.totalElements;
          this.pagination.totalPages = res.totalPages;
          this.pagination.page = res.page
          this.pagination.size = res.size;
          this.loading = false;
          this.cdr.detectChanges();
        },
        error: () => {
          this.loading = false;
          this.cdr.detectChanges();
        }
      });
  }

  /** Handlers pour ton composant Pagination */
  handlePageChange(newPage: number) {
    this.pagination.page = newPage;
    this.loadLicenses();
  }

  handleSizeChange(newSize: number) {
    this.pagination.size = newSize;
    this.pagination.page = 0; // Reset à la première page
    this.loadLicenses();
  }

  download(license: LicenseResponse) {

  }

  createNewLicense() {
    this.showLicenseCreationModal = true;
  }

  onCreateLicense() {
    this.showLicenseCreationModal = false;
    this.pagination.page = 1;
    this.loadLicenses();
  }

  toggleLicenseStatus(license: LicenseResponse) {
    const newStatus = !license.active;
    // On pourrait appeler un endpoint partiel ou le update général
    /*this.licenseService.updateLicenseStatus(license.id, newStatus).subscribe({
      next: () => {
        license.active = newStatus;
        this.cdr.detectChanges();
      }
    });*/
  }

  /** Action : Ouvrir le modal d'édition des paramètres */
  editParameters(license: LicenseResponse) {
    // Ici, tu peux ouvrir le même modal que la création mais en mode "Edit"
    // ou un modal spécifique aux paramètres
    console.log("Editing parameters for:", license.id);
    // this.selectedLicense = license;
    // this.showEditModal = true;
  }

  /** Action : Régénérer le code d'activation ou rafraîchir les données */
  displayLicenseExport(license: LicenseResponse) {
    if(confirm("Regenerate the signed key for this license?")) {
      /*this.licenseService.generateLicense(license.id).subscribe(() => {
        this.loadLicenses(); // Recharger pour voir les changements
      });*/
    }
  }

  /** Action : Supprimer/Révoquer */
  deleteLicense(license: LicenseResponse) {
    if(confirm("CRITICAL: Revoke this license protocol permanently?")) {
      this.licenseService.deleteLicense(license.id).subscribe({
        next: () => this.loadLicenses()
      });
    }
  }

  copyToClipboard(text: string) {
    navigator.clipboard.writeText(text).then(() => {
      // Optionnel : Tu peux ajouter une notification de succès ici
      console.log('Key copied to clipboard');
    }).catch(err => {
      console.error('Could not copy text: ', err);
    });
  }
  downloadLicense($event: { raison: string }) {
    if (!this.selectedLicense?.id) return;

    this.licenseService.downloadLicenseFile(this.selectedLicense.id, $event.raison).subscribe({
      next: (blob: Blob) => {
        // 1. Créer une URL pour le Blob
        const url = window.URL.createObjectURL(blob);
        // 2. Créer un élément <a> invisible pour déclencher le téléchargement
        const link = document.createElement('a');
        link.href = url;

        // 3. Définir le nom du fichier (ex: LIC-CLIENT-PROJET.bin)
        link.download = `LIC_${this.selectedLicense?.clientName.toUpperCase()}_${this.selectedLicense?.projectName.toUpperCase()}.lic`;

        // 4. Déclencher le clic et nettoyer
        link.click();
        window.URL.revokeObjectURL(url);

        this.notifyService.success('LICENSE_EXPORTED_SUCCESSFULLY');
        this.isExportLicenseModal = false;
      },
      error: (err) => {
        console.error('Export failed', err);
        this.notifyService.error('EXPORT_FAILED_CHECK_LOGS');
        this.isExportLicenseModal = false;
      }
    });
  }
}
