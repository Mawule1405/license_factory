import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pagination.component.html'
})
export class PaginationComponent {

  @Input() message = "Éléments";
  @Input() pageSizeOptions: number[] = [10, 25, 50, 100];
  @Input() currentPage: number = 1;
  @Input() totalPages: number = 0;
  @Input() totalElements: number = 0;
  @Input() itemsPerPage: number = 10;

  @Output() pageChanged = new EventEmitter<number>();
  @Output() pageSizeChanged = new EventEmitter<number>();

  get startEntry(): number {
    if (this.totalElements === 0) return 0;
    // On s'assure que currentPage est au minimum 1
    const page = Math.max(1, this.currentPage);
    return (page - 1) * this.itemsPerPage + 1;
  }

  get endEntry(): number {
    if (this.totalElements === 0) return 0;
    return Math.min(this.currentPage * this.itemsPerPage, this.totalElements);
  }

  get pages(): number[] {
    const pages: number[] = [];
    const maxVisiblePages = 5;

    // Logique de fenêtre centrée sur la page courante
    let start = Math.max(1, this.currentPage - Math.floor(maxVisiblePages / 2));
    let end = Math.min(this.totalPages, start + maxVisiblePages - 1);

    if (end - start < maxVisiblePages - 1) {
      start = Math.max(1, end - maxVisiblePages + 1);
    }

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    return pages;
  }

  onPageClick(page: number): void {
    if (page >= 1 && page <= this.totalPages && page !== this.currentPage) {
      this.pageChanged.emit(page);
    }
  }

  onPageSizeChange(event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    const newSize = Number(selectElement.value);
    this.pageSizeChanged.emit(newSize);
  }
}
