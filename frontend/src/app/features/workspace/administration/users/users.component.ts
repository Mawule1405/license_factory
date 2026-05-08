import {Component, OnInit, inject, ChangeDetectorRef} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';
import {UserService} from '../../../../core/services/user.service';
import {AppUser} from '../../../../core/models/auth.model';
import {CreateUserModalComponent} from './create-user-modal/create-user-modal.component';
import {PaginationComponent} from '../../../../shared/components/layout/pagination/pagination.component';
import {EditUserModalComponent} from './edit-user-modal/edit-user-modal.component';
import {NotificationService} from '../../../../core/services/notification.service';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule, CreateUserModalComponent, PaginationComponent
    , EditUserModalComponent],
  templateUrl: './users.component.html'
})
export class UsersComponent implements OnInit {
  private userService = inject(UserService);
  private notifyService = inject(NotificationService)
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


  initializePassword(user: AppUser) {
    this.notifyService.confirm(
      `Are you sure you want to reset the password for ${user.username}?`,
      "PASSWORD_RESET_PROTOCOL"
    ).then((confirmed) => {
      if (confirmed) {
        this.userService.resetPassword(user.id).subscribe({
          next: () => {
            this.notifyService.success(
              `New credentials dispatched to ${user.email}`,
              "RESET_SUCCESS"
            );
          },
          error: (err) => {
            this.notifyService.error(
              "Failed to initialize reset protocol",
              "EXECUTION_ERROR"
            );
          }
        });
      }
    });
  }

  revokeAccess(user: AppUser) {
    this.notifyService.confirm(
      `CRITICAL: You are about to revoke all access for ${user.fullName}. This action is logged.`,
      "REVOKE_ACCESS_CONFIRMATION"
    ).then((confirmed) => {
      if (confirmed) {
        this.userService.revokeUser(user.id).subscribe({
          next: () => {
            this.notifyService.success(
              `User ${user.username} has been purged from registry`,
              "REVOCATION_COMPLETE"
            );
            // Optionnel : Déclencher un rafraîchissement de la liste
            this.loadUsers();
          },
          error: (err) => {
            this.notifyService.error(
              "Security clearance level insufficient or network error",
              "REVOCATION_FAILED"
            );
          }
        });
      }
    });
  }

  deleteOperator(user: AppUser) {
    // 1. Demande de confirmation avec un ton "Security Protocol"
    this.notifyService.confirm(
      `CRITICAL: Are you sure you want to permanently delete operator [ ${user.username} ]? This action cannot be undone.`,
      "TERMINATION_CONFIRMATION"
    ).then((confirmed) => {
      if (confirmed) {
        // 2. Activation du flag de chargement global si nécessaire ou feedback visuel
        this.userService.deleteUser(user.id).subscribe({
          next: () => {
            // 3. Notification de succès
            this.notifyService.success(
              `Operator ${user.username} successfully purged from system registry.`,
              "DELETION_SUCCESS"
            );

            this.loadUsers();
          },
          error: (err) => {
            // 6. Gestion d'erreur
            this.notifyService.error(
              "Access denied or system error during deletion protocol.",
              "DELETION_FAILED"
            );
            console.error('Termination error:', err);
          }
        });
      }
    });
  }

}
