import { Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ClientService } from '../../../../../core/services/client.service';
import {
  NotificationModalComponent
} from '../../../../../shared/components/dialogs/notification-modal/notification-modal.component';
import {NotificationService} from '../../../../../core/services/notification.service';

@Component({
  selector: 'app-create-client-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-client-modal.component.html'
})
export class CreateClientModalComponent {
  private fb = inject(FormBuilder);
  private clientService = inject(ClientService);
  private  notifyService = inject(NotificationService)

  @Output() close = new EventEmitter<void>();
  @Output() created = new EventEmitter<void>();

  showSuccess: boolean = false;

  clientForm: FormGroup = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', [Validators.required]],
    address: ['', [Validators.required]]
  });

  onSubmit() {
    if (this.clientForm.valid) {
      this.clientService.saveClient(this.clientForm.value).subscribe({
        next: () => {
          this.created.emit();
          this.close.emit();
        },
        error: err => {
          console.log(err);
        }
      });
    }
  }
}
