import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormArray } from '@angular/forms';
import { ProjectService } from '../../../../../core/services/project.service';
import { NotificationService } from '../../../../../core/services/notification.service';
import { finalize } from 'rxjs';
import { Project } from '../../../../../core/models/project.model'; // Importe ton interface

@Component({
  selector: 'app-edit-license-model-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './edit-license-model-modal.component.html'
})
export class EditLicenseModelModalComponent implements OnInit {
  private fb = inject(FormBuilder);
  private projectService = inject(ProjectService);
  private notifyService = inject(NotificationService);

  @Input({ required: true }) project!: Project; // Le projet à modifier
  @Output() close = new EventEmitter<void>();
  @Output() updated = new EventEmitter<void>();

  isLoading: boolean = false;

  projectForm: FormGroup = this.fb.group({
    name: ['', [Validators.required]],
    description: ['', [Validators.required]],
    licenseModel: this.fb.group({
      parameters: this.fb.array([]) // Initialement vide pour le remplissage dynamique
    })
  });

  ngOnInit() {
    this.patchProjectData();
  }

  // Remplit le formulaire avec les données existantes du projet
  private patchProjectData() {
    if (this.project) {
      this.projectForm.patchValue({
        name: this.project.name,
        description: this.project.description
      });

      // Remplissage du FormArray des paramètres
      const params = this.project.licenseModel?.parameters || [];
      params.forEach(param => {
        this.parameters.push(this.fb.control(param, Validators.required));
      });

      // Sécurité : Si aucun paramètre n'existe, en ajouter un vide
      if (this.parameters.length === 0) {
        this.addParameter();
      }
    }
  }

  get parameters() {
    return this.projectForm.get('licenseModel.parameters') as FormArray;
  }

  addParameter() {
    this.parameters.push(this.fb.control('', Validators.required));
  }

  removeParameter(index: number) {
    if (this.parameters.length > 1) {
      this.parameters.removeAt(index);
    }
  }

  onSubmit() {
    if (this.projectForm.valid && !this.isLoading) {
      this.isLoading = true;

      // On utilise updateProject au lieu de createProject
      this.projectService.updateProject(this.project.id!, this.projectForm.value)
        .pipe(finalize(() => this.isLoading = false))
        .subscribe({
          next: () => {
            this.notifyService.success("Security parameters synchronized successfully.", "SYNC_OK");
            this.updated.emit();
            this.close.emit();
          },
          error: (err) => console.error(err)
        });
    }
  }
}
