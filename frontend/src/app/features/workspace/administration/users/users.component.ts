import {Component, OnInit, inject, ChangeDetectorRef} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';
import {UserService} from '../../../../core/services/user.service';
import {AppUser} from '../../../../core/models/auth.model';
import {CreateUserModalComponent} from './create-user-modal/create-user-modal.component';
import {PaginationComponent} from '../../../../shared/components/layout/pagination/pagination.component';
import {EditUserModalComponent} from './edit-user-modal/edit-user-modal.component';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule, CreateUserModalComponent, PaginationComponent
    , EditUserModalComponent],
  templateUrl: './users.component.html'
})
export class UsersComponent implements OnInit {
  private userService = inject(UserService);
  private cdr = inject(ChangeDetectorRef)

  // Data
  users: AppUser[] = [];
  isCreateModalOpen = false;
  isEditModalOpen = false
  selectedUser! : AppUser;

  // State Management
  loading = false;
  searchTerm = '';


  // Search Optimization
  private searchSubject = new Subject<string>();
  pagination = {
    currentPage: 1,
    totalPages: 0,
    totalElements: 0,
    page:0,
    size:10,
  };

  ngOnInit() {
    this.searchSubject.pipe(
      debounceTime(200),
      distinctUntilChanged()
    ).subscribe(() => {
      this.loadUsers();
    });

    this.loadUsers();
  }

  loadUsers() {
    this.loading = true;
    this.userService.searchUsers(this.searchTerm, this.pagination.currentPage, this.pagination.size)
      .subscribe({
        next: (response) => {
          this.users = response.content;
          this.pagination.totalElements = response.totalElements;
          this.pagination.totalPages = response.totalPages;
          this.pagination.currentPage = response.page;
          this.pagination.page = response.page;
          this.pagination.size = response.size;
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

  deleteOperator(user: AppUser) {
    if (confirm(`CRITICAL: CONFIRM TERMINATION OF OPERATOR [${user.username.toUpperCase()}]?`)) {
      this.userService.deleteUser(user.id).subscribe(() => this.loadUsers());
    }
  }

  openCreationPanel() {
    this.isCreateModalOpen = true
  }

  // Dans le parent
  handlePageChange(page: number) {
    this.pagination.page = page;
    this.loadUsers();
  }

  handleSizeChange(size: number) {
    this.pagination.size = size;
    this.pagination.page = 1; // Toujours revenir à la page 1
    this.loadUsers();
  }

  editUser(user: AppUser) {
    this.selectedUser = user;
    this.isEditModalOpen = true;
  }


}
