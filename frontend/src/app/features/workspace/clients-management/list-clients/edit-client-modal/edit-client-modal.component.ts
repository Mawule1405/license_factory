import { Component, EventEmitter, Input, Output, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ClientService } from '../../../../../core/services/client.service';
import { NotificationService } from '../../../../../core/services/notification.service';

@Component({
  selector: 'app-edit-client-modal', // Changement du sélecteur
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './edit-client-modal.component.html'
})
export class EditClientModalComponent implements OnInit {
  private fb = inject(FormBuilder);
  private clientService = inject(ClientService);
  private notifyService = inject(NotificationService);

  @Input() client: any; // Données du client passées par le parent
  @Output() close = new EventEmitter<void>();
  @Output() updated = new EventEmitter<void>(); // Renommé created -> updated

  clientForm: FormGroup = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', [Validators.required]],
    address: ['', [Validators.required]]
  });

  ngOnInit() {
    if (this.client) {
      // Injection des données existantes dans le formulaire
      this.clientForm.patchValue({
        name: this.client.name,
        email: this.client.email,
        phone: this.client.phone,
        address: this.client.address
      });
    }
  }

  onSubmit() {
    if (this.clientForm.valid) {
      // Utilisation de l'ID du client pour la mise à jour
      const updatedClient = { ...this.clientForm.value, id: this.client.id };

      this.clientService.updateClient(updatedClient).subscribe({
        next: () => {
          this.updated.emit();
          this.close.emit();
          // Notification optionnelle via notifyService ici
        },
        error: err => {
          console.error('Update_Error:', err);
        }
      });
    }
  }
}
