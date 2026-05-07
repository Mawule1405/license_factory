import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../../../../core/services/user.service';
import {AppUser} from '../../../../../core/models/auth.model';

@Component({
  selector: 'app-edit-user-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './edit-user-modal.component.html'
})
export class EditUserModalComponent implements OnInit {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);

  @Input({ required: true }) user!: AppUser; // L'utilisateur envoyé par le composant parent
  @Output() close = new EventEmitter<void>();
  @Output() updated = new EventEmitter<void>();

  isLoading = false;

  userForm: FormGroup = this.fb.group({
    username: [{ value: '', disabled: true }, [Validators.required]], // Identifiant souvent non modifiable
    fullName: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.minLength(8)]], // Facultatif lors d'une mise à jour
    role: ['OPERATOR', Validators.required]
  });

  ngOnInit() {
    if (this.user) {
      // Extraction du rôle (on suppose que l'user arrive avec un tableau appRoles)
      const currentRole = this.user.appRoles && this.user.appRoles.length > 0
        ? this.user.appRoles[0].name
        : 'OPERATOR';

      this.userForm.patchValue({
        username: this.user.username,
        fullName: this.user.fullName,
        email: this.user.email,
        role: currentRole
      });
    }
  }

  onSubmit() {
    if (this.userForm.valid) {
      this.isLoading = true;

      // Préparation du payload
      const formData: any = {
        fullName: this.userForm.value.fullName,
        email: this.userForm.value.email,
        appRoles: [{ name: this.userForm.value.role }]
      };

      // On n'envoie le mot de passe que s'il a été saisi
      if (this.userForm.value.password) {
        formData.password = this.userForm.value.password;
      }

      this.userService.updateProfile(this.user.id, formData).subscribe({
        next: () => {
          this.updated.emit();
          this.close.emit();
        },
        error: (err) => {
          console.error(err);
          this.isLoading = false;
        }
      });
    }
  }
}
