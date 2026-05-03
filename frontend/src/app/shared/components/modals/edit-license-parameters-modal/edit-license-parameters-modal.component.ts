import { Component, EventEmitter, Input, OnInit, Output, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormControl } from '@angular/forms';
import {LicenseService} from '../../../../core/services/license.service';
import {LicenseResponse} from '../../../../core/models/license.model';

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
  dynamicParamKeys: string[] = [];
  isLoading = false;

  ngOnInit() {
    this.initForm();
    this.loadCurrentLicenseData();
  }

  initForm() {
    this.licenseForm = this.fb.group({
      // En mode édition, le projet est généralement fixe, on le met en readonly
      projectId: [{ value: '', disabled: true }],
      dynamicParams: this.fb.group({})
    });
  }

  loadCurrentLicenseData() {
    if (!this.license) return;

    // 1. On définit le projet (ID ou Nom pour l'affichage)
    this.licenseForm.patchValue({ projectId: this.license.projectId });

    // 2. On génère les champs basés sur les clés présentes dans la licence actuelle
    const dynamicGroup = this.licenseForm.get('dynamicParams') as FormGroup;
    this.dynamicParamKeys = Object.keys(this.license.parameters);

    this.dynamicParamKeys.forEach(key => {
      // On crée le contrôle avec la valeur actuelle de la licence
      const currentValue = this.license.parameters[key];
      dynamicGroup.addControl(key, new FormControl(currentValue, Validators.required));
    });

    this.cdr.detectChanges();
  }

  onSubmit() {
    if (this.licenseForm.invalid) return;
    this.isLoading = true;

    // On ne récupère que les paramètres modifiés
    const payload = {
      parameters: this.licenseForm.get('dynamicParams')?.value
    };

    // Appel au service de mise à jour (supposé updateLicense ou patchLicense)
    this.licenseService.updateLicense(this.license.id, payload).subscribe({
      next: () => {
        this.updated.emit();
        this.close.emit();
      },
      error: () => this.isLoading = false
    });
  }
}
