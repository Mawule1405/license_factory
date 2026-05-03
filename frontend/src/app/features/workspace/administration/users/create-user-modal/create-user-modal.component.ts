import { Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import {UserService} from '../../../../../core/services/user.service';


@Component({
  selector: 'app-create-user-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-user-modal.component.html'
})
export class CreateUserModalComponent {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);

  @Output() close = new EventEmitter<void>();
  @Output() userCreated = new EventEmitter<void>();

  userForm: FormGroup = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    fullName: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    role: ['OPERATOR', Validators.required]
  });

  onSubmit() {
    if (this.userForm.valid) {
      // Transformation pour correspondre à ton DTO (List<AppRoleDTO>)
      const formData = {
        ...this.userForm.value,
        appRoles: [{ name: this.userForm.value.role}]
      };

      this.userService.createUser(formData).subscribe({
        next: () => {
          this.userCreated.emit();
          this.close.emit();
        },
        error: (err) => console.log(err)
      });
    }
  }
}
