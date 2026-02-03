import {Component, OnInit, inject, ChangeDetectorRef} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';
import {UserService} from '../../../../core/services/user.service';
import {AppUser} from '../../../../core/models/auth.model';
import {CreateUserModalComponent} from './create-user-modal/create-user-modal.component';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule, CreateUserModalComponent],
  templateUrl: './users.component.html'
})
export class UsersComponent implements OnInit {
  private userService = inject(UserService);
  private cdr = inject(ChangeDetectorRef)

  // Data
  users: AppUser[] = [];
  isCreateModalOpen = false;

  // State Management
  loading = false;
  searchTerm = '';
  currentPage = 0;
  pageSize = 5;
  totalElements = 0;
  totalPages = 0;

  // Search Optimization
  private searchSubject = new Subject<string>();

  ngOnInit() {
    this.searchSubject.pipe(
      debounceTime(200),
      distinctUntilChanged()
    ).subscribe(() => {
      this.currentPage = 0;
      this.loadUsers();
    });

    this.loadUsers();
  }

  loadUsers() {
    this.loading = true;
    this.userService.searchUsers(this.searchTerm, this.currentPage, this.pageSize)
      .subscribe({
        next: (response) => {
          this.users = response.content;
          this.totalElements = response.totalElements;
          this.totalPages = response.totalPages;
          this.loading = false;
          this.cdr.detectChanges()
        },
        error: (err) => {
          console.error('Data stream interrupted', err);
          this.loading = false;
          this.cdr.detectChanges()
        }
      });
  }

  onSearchChange() {
    this.searchSubject.next(this.searchTerm);
  }

  changePage(delta: number) {
    const nextSub = this.currentPage + delta;
    if (nextSub >= 0 && nextSub < this.totalPages) {
      this.currentPage = nextSub;
      this.loadUsers();
    }
  }

  deleteOperator(user: AppUser) {
    if (confirm(`CRITICAL: CONFIRM TERMINATION OF OPERATOR [${user.username.toUpperCase()}]?`)) {
      this.userService.deleteUser(user.id).subscribe(() => this.loadUsers());
    }
  }

  openCreationPanel() {
    this.isCreateModalOpen = true
  }
}
