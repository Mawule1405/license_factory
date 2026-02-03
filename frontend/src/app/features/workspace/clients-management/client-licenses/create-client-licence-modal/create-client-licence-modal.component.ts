import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import {LicenseService} from '../../../../../core/services/license.service';
import {LicenseLevel} from '../../../../../core/models/license.model';

@Component({
  selector: 'app-create-client-licence-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-client-licence-modal.component.html'
})
export class CreateClientLicenceModalComponent {
  private fb = inject(FormBuilder);
  private licenseService = inject(LicenseService);

  @Input() clientId!: string; // Reçu du composant parent (ClientLicenses)
  @Output() close = new EventEmitter<void>();
  @Output() created = new EventEmitter<void>();

  levels = Object.values(LicenseLevel);

  licenseForm: FormGroup = this.fb.group({
    level: [LicenseLevel.FREEMIUM, Validators.required],
    addressMac: ['', [Validators.required, Validators.pattern('^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$')]],
    maxUsers: [1, [Validators.required, Validators.min(1)]],
    expiryDate: ['', Validators.required]
  });

  onSubmit() {
    if (this.licenseForm.valid) {

      const payload = {
        ...this.licenseForm.value,
        clientId:this.clientId,
      };

      this.licenseService.saveLicense(payload).subscribe({
        next: () => {
          this.created.emit();
          this.close.emit();
        },
        error: (err) => console.error('Protocol generation failed', err)
      });
    }
  }
}
