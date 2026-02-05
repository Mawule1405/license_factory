import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NotificationService } from '../../../core/services/notification.service';
import { UserService } from '../../../core/services/user.service';
import { StorageService } from '../../../core/services/storage.service';
import { ACCESS_TOKEN } from '../../../core/constants/auth.constants';
import { AppUser } from '../../../core/models/auth.model';
import { DatePipe, CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    DatePipe,
    CommonModule,
    RouterLink
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css',
})
export class ProfileComponent implements OnInit {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private notifService = inject(NotificationService);
  private storage = inject(StorageService);

  profileForm!: FormGroup;
  currentUser: AppUser | null = null;
  currentUserId = "";
  isLoading = false;

  ngOnInit(): void {
    // Initialisation d'un formulaire vide pour éviter les erreurs "undefined" au premier rendu
    this.initEmptyForm();

    this.currentUserId = this.storage.getUserIdFromToken(ACCESS_TOKEN);

    if (this.currentUserId) {
      this.loadUser(this.currentUserId);
    } else {
      this.notifService.error("Session invalide. Veuillez vous reconnecter.", "AUTH ERROR");
    }
  }

  private initEmptyForm() {
    this.profileForm = this.fb.group({
      username: [{ value: '', disabled: false }, [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      fullName: ['', [Validators.required, Validators.minLength(3)]]
    });
  }

  loadUser(userId: string) {
    this.userService.getUser(userId).subscribe({
      next: (response) => {
        this.currentUser = response;
        // Mettre à jour le formulaire avec les données reçues
        this.profileForm.patchValue({
          username: this.currentUser?.username,
          email: this.currentUser?.email,
          fullName: this.currentUser?.fullName
        });
      },
      error: (error) => {
        this.notifService.error("Impossible de charger les données du profil.", "SERVER ERROR");
        console.error(error);
      }
    });
  }

  getInitials(name?: string): string {
    if (!name) return '??';
    const parts = name.trim().split(' ');
    if (parts.length === 1) return parts[0].substring(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }

  onUpdateProfile() {
    if (this.profileForm.valid && this.currentUserId) {
      this.isLoading = true;
      let data = this.profileForm.value;
      data.id = this.currentUser?.id;

      this.userService.updateProfile(this.currentUserId, data).subscribe({
        next: (updatedUser) => {
          this.loadUser(this.currentUserId)
          this.notifService.success('Profil mis à jour avec succès. \n Si vous avez modifié le username, veuillez-vous reconnecter.', 'SUCCESS');
          this.isLoading = false;
          this.profileForm.markAsPristine();
        },
        error: (err) => {
          this.notifService.error('Erreur lors de la mise à jour', 'ERROR');
          this.isLoading = false;
        }
      });
    }
  }

  resetForm() {
    if (this.currentUser) {
      this.profileForm.patchValue({
        username: this.currentUser.username,
        email: this.currentUser.email,
        fullName: this.currentUser.fullName
      });
      this.profileForm.markAsPristine();
    }
  }
}
