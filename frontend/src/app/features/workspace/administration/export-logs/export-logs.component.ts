import {Component, OnInit, inject, ChangeDetectorRef} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExportService } from '../../../../core/services/export.service';
import { ExportResponse } from '../../../../core/models/export_response.model';
import { Pagination } from '../../../../core/models/auth.model';
import {PaginationComponent} from '../../../../shared/components/layout/pagination/pagination.component';

@Component({
  selector: 'app-export-logs',
  standalone: true,
  imports: [CommonModule, PaginationComponent],
  templateUrl: './export-logs.component.html',
  styleUrl: './export-logs.component.css'
})
export class ExportLogsComponent implements OnInit {
  private exportService = inject(ExportService);
  private cdr = inject(ChangeDetectorRef)

  // État des données
  exports: ExportResponse[] = [];
  isLoading = false;

  // État de la pagination
  pagination: Pagination<ExportResponse> = {
    content: [],
    totalElements: 0,
    totalPages: 0,
    page: 1, // Basé sur ton choix de commencer à 1
    size: 10,
  };

  ngOnInit(): void {
    this.loadExports();
  }

  loadExports(): void {
    this.isLoading = true;
    this.exportService.fetchAllExports(this.pagination.page, this.pagination.size).subscribe({
      next: (res) => {
        this.exports = res.content;
        this.pagination = res;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
        // Optionnel : notification d'erreur ici
      }
    });
  }

  handlePageChange(newPage: number): void {
    this.pagination.page = newPage;
    this.loadExports();
  }

  handleSizeChange(newSize: number): void {
    this.pagination.size = newSize;
    this.pagination.page = 1; // Retour à la première page lors du changement de taille
    this.loadExports();
  }
}
