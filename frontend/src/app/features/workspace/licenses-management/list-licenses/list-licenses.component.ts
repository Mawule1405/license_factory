import {ChangeDetectorRef, Component, inject, OnInit} from '@angular/core';
import {LicenseService} from '../../../../core/services/license.service'; // Service à adapter
import {LicenseResponse} from '../../../../core/models/license.model'; // Modèle à adapter
import {FormsModule} from '@angular/forms';

import {PaginationComponent} from '../../../../shared/components/layout/pagination/pagination.component';
import {CommonModule, DatePipe} from '@angular/common';
import {CreateLicenseModalComponent} from './create-license-modal/create-license-modal.component';
import {LicenseParametersModalComponent} from './license-parameters-modal/license-parameters-modal.component';
import {NotificationService} from '../../../../core/services/notification.service';
import {ExportLicenseRaisonModalComponent} from '../../../../shared/components/modals/export-license-raison-modal/export-license-raison-modal.component';
import {
  LicenseExportLogsModalComponent
} from '../../../../shared/components/modals/license-export-logs-modal/license-export-logs-modal.component';
import {
  EditLicenseParametersModalComponent
} from '../../../../shared/components/modals/edit-license-parameters-modal/edit-license-parameters-modal.component';

@Component({
  selector: 'app-list-licenses',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    PaginationComponent,
    DatePipe,
    CreateLicenseModalComponent,
    LicenseParametersModalComponent,
    ExportLicenseRaisonModalComponent,
    LicenseExportLogsModalComponent,
    EditLicenseParametersModalComponent
  ],
  templateUrl: './list-licenses.component.html',
})
export class ListLicensesComponent implements OnInit {

  private licenseService = inject(LicenseService);
  private notifyService = inject(NotificationService);
  private cdr = inject(ChangeDetectorRef);

  licenses: LicenseResponse[] = [];
  loading = false;
  searchKey = '';
  isModalOpen = false

  pagination = {
    totalPages: 0,
    totalElements: 0,
    page: 1,
    size: 10,
  };

  isLicenseParametersModal = false;
  isExportLicenseModal= false
  isLogsModal = false
  isEditLicenseModal = false
  selectedLicense? : LicenseResponse

  ngOnInit() {
    this.loadLicenses();
  }

  loadLicenses() {
    this.loading = true;
    // On suppose que votre service a une méthode fetchAllLicenses pour une vue globale
    this.licenseService.fetchLicenses(this.pagination.page, this.pagination.size, this.searchKey)
      .subscribe({
        next: (res) => {
          this.loading = false;
          this.licenses = res.content;
          this.pagination.totalElements = res.totalElements;
          this.pagination.totalPages = res.totalPages;
          this.cdr.detectChanges();
        },
        error: () => {
          this.loading = false;
          this.cdr.detectChanges();
        }
      });
  }

  onSearch() {
    this.pagination.page = 1;
    this.loadLicenses();
  }

  handlePageChange(page: number) {
    this.pagination.page = page;
    this.loadLicenses();
  }

  handleSizeChange(size: number) {
    this.pagination.size = size;
    this.pagination.page = 1;
    this.loadLicenses();
  }

  viewParameters(license: LicenseResponse) {
    this.selectedLicense = license;
    this.isLicenseParametersModal = true;
  }

  editParameters(license: LicenseResponse) {
    this.selectedLicense = license;
    this.isEditLicenseModal = true;
  }

  exportLicense(license: LicenseResponse) {
  this.selectedLicense = license;
  this.isExportLicenseModal = true;
  }

  copyToClipboard(activationCode: string) {
    if (!activationCode) {
      this.notifyService.error('EMPTY_CODE_ERROR');
      return;
    }

    navigator.clipboard.writeText(activationCode).then(
      () => {

      },
      (err) => {
        console.error('Could not copy text: ', err);

      }
    );
  }

  viewLicenseLogs(license: LicenseResponse) {
    this.selectedLicense = license
    this.isLogsModal = true
  }

  revokeLicense(id: string) {
    this.notifyService.confirm("Do you want to revoke this license", "REVOKE LICENSE").then(result => {
      if (result) {

        this.licenseService.deleteLicense(id).subscribe({
          next: () => {
            this.notifyService.success('LICENSE_REVOKED_SUCCESSFULLY');
            this.loadLicenses();
          },
          error: (err) => {
            console.error('Revocation failed', err);

          }
        });
      }
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
