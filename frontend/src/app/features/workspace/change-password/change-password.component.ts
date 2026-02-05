import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { UserService } from '../../../core/services/user.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Router, RouterLink } from '@angular/router';
import {StorageService} from '../../../core/services/storage.service';
import {ACCESS_TOKEN} from '../../../core/constants/auth.constants';
import {AppUser} from '../../../core/models/auth.model';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './change-password.component.html'
})
export class ChangePasswordComponent implements OnInit {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private notifService = inject(NotificationService);
  private router = inject(Router);
  private storage = inject(StorageService)

  passwordForm!: FormGroup;
  isLoading = false;
  strength = 0;

  showOld = false;
  showNew = false;

  currentUserId: string="";
  currentUser : AppUser|null=null;

  ngOnInit(): void {

    this.currentUserId = this.storage.getUserIdFromToken(ACCESS_TOKEN)
    if(this.currentUserId){
      this.passwordForm = this.fb.group({
        oldPassword: ['', [Validators.required]],
        newPassword: ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', [Validators.required]]
      }, { validators: this.passwordMatchValidator });

      this.loadUser(this.currentUserId)
    }


  }


  loadUser(userId : string) {
    this.userService.getUser(userId).subscribe({
      next: (res) => {
        this.currentUser = res;
      },
      error: (err) => {
        console.log(err);
      }
    })
  }


  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('newPassword');
    const confirm = control.get('confirmPassword');
    return password && confirm && password.value !== confirm.value ? { mismatch: true } : null;
  }

  checkStrength() {
    const password = this.passwordForm.get('newPassword')?.value || '';
    let s = 0;
    if (password.length >= 8) s++;
    if (/[A-Z]/.test(password)) s++;
    if (/[0-9]/.test(password)) s++;
    if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) s++;
    this.strength = s;
  }

  getStrengthLabel() {
    const labels = ['Invalide', 'Faible', 'Moyen', 'Fort', 'Excellent'];
    return labels[this.strength];
  }

  getStrengthColor() {
    const colors = ['text-gray-400', 'text-red-500', 'text-orange-500', 'text-yellow-600', 'text-taurus-green'];
    return colors[this.strength];
  }

  onSubmit() {
    if (this.passwordForm.valid) {
      this.isLoading = true;
      const { oldPassword, newPassword } = this.passwordForm.value;

      this.userService.changePassword(this.currentUserId,oldPassword, newPassword).subscribe({
        next: () => {
          this.notifService.success('Mot de passe modifié avec succès', 'SÉCURITÉ');
          this.isLoading = false;
          this.router.navigate(['/login']);
        },
        error: (err) => {
          this.notifService.error(err.error?.message || 'Échec de la modification', 'ERREUR');
          this.isLoading = false;
        }
      });
    }
  }
}
