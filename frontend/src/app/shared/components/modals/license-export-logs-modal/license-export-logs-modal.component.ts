import {Component, EventEmitter, Input, OnInit, Output, inject, ChangeDetectorRef} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExportResponse } from '../../../../core/models/export_response.model';
import { ExportService } from '../../../../core/services/export.service';
import { Pagination } from '../../../../core/models/auth.model';

@Component({
  selector: 'app-license-export-logs-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './license-export-logs-modal.component.html'
})
export class LicenseExportLogsModalComponent implements OnInit {
  private exportService = inject(ExportService);
  private cdr = inject(ChangeDetectorRef)

  @Input({ required: true }) licenseId!: string;
  @Input({ required: true }) activationCode!: string;
  @Output() close = new EventEmitter<void>();

  logs: ExportResponse[] = [];
  currentPage = 1; // Initialisé à 1
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;
  isLoading = false;

  ngOnInit() {
    this.loadLogs(this.currentPage);
  }

  loadLogs(page: number = 1) {
    this.isLoading = true;
    this.exportService.fetchExportsByLicense(this.licenseId, page, this.pageSize).subscribe({
      next: (res: Pagination<ExportResponse>) => {
        this.logs = res.content;
        this.totalElements = res.totalElements;
        this.totalPages = res.totalPages;
        // On s'assure de synchroniser avec la page retournée par l'API
        this.currentPage = res.page;
        this.isLoading = false;
        this.cdr.detectChanges()
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  nextPage() {
    // Si on est à la page 1 et qu'il y a 3 pages, on peut aller à la suivante
    if (this.currentPage < this.totalPages) {
      this.loadLogs(this.currentPage + 1);
    }
  }

  prevPage() {
    // La limite inférieure est maintenant 1
    if (this.currentPage > 1) {
      this.loadLogs(this.currentPage - 1);
    }
  }
}
