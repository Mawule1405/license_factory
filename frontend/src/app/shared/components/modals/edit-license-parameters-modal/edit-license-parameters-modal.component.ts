import { Component, EventEmitter, Input, OnInit, Output, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormControl } from '@angular/forms';
import {LicenseService} from '../../../../core/services/license.service';
import {LicenseParameter, LicenseResponse} from '../../../../core/models/license.model';

@Component({
  selector: 'app-edit-license-parameters-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './edit-license-parameters-modal.component.html'
})
export class EditLicenseParametersModalComponent implements OnInit {
  private fb = inject(FormBuilder);
  private licenseService = inject(LicenseService);
  private cdr = inject(ChangeDetectorRef);

  @Input({ required: true }) license!: LicenseResponse; // La licence à modifier
  @Output() close = new EventEmitter<void>();
  @Output() updated = new EventEmitter<void>(); // Événement de succès différent

  licenseForm!: FormGroup;
  dynamicParameters: LicenseParameter[] = [];
  isLoading = false;

  ngOnInit() {
    this.initForm();
    this.loadCurrentLicenseData();
  }

  initForm() {
    this.licenseForm = this.fb.group({
      projectId: [{ value: '', disabled: true }],
      dynamicParams: this.fb.group({})
    });
  }

  loadCurrentLicenseData() {
    if (!this.license || !this.license.parameters) return;

    this.licenseForm.patchValue({ projectId: this.license.projectId });

    const dynamicGroup = this.licenseForm.get('dynamicParams') as FormGroup;

    // On récupère le tableau d'objets paramètres
    this.dynamicParameters = this.license.parameters;

    this.dynamicParameters.forEach(param => {
      // On utilise param.label comme clé du formulaire et param.value comme valeur initiale
      dynamicGroup.addControl(
        param.label,
        new FormControl(param.value, Validators.required)
      );
    });

    this.cdr.detectChanges();
  }

  onSubmit() {
    if (this.licenseForm.invalid) return;
    this.isLoading = true;

    // 1. Récupérer les valeurs saisies dans le groupe dynamique { "ADRESS_MAC": "...", "MAX_USERS": "..." }
    const dynamicValues = this.licenseForm.get('dynamicParams')?.value;

    // 2. Transformer en tableau d'objets LicenseParameterDto
    // On utilise this.dynamicParameters (chargé au ngOnInit) pour conserver les types et IDs
    const formattedParameters = this.dynamicParameters.map(param => ({
      id: param.id,             // Optionnel mais recommandé pour l'update
      label: param.label,       // Le label sert de clé technique
      value: String(dynamicValues[param.label]), // La nouvelle valeur saisie
      type: param.type          // On conserve le type d'origine
    }));

    // 3. Construction du payload final
    const payload = {
      parameters: formattedParameters
    };

    this.licenseService.updateLicense(this.license.id, payload).subscribe({
      next: () => {
        this.updated.emit();
        this.close.emit();
      },
      error: () => {
        this.isLoading = false;
        // Optionnel : notify error
      }
    });
  }

}
