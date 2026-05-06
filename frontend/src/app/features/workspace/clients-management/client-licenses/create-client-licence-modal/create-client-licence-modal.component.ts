import {Component, EventEmitter, Input, Output, OnInit, inject, ChangeDetectorRef} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormControl } from '@angular/forms';
import { LicenseService } from '../../../../../core/services/license.service';
import { ProjectService } from '../../../../../core/services/project.service';
import {Project} from '../../../../../core/models/project.model';
import {Parameter} from '../../../../../core/models/license-model.model'; // Service à importer

@Component({
  selector: 'app-create-client-licence-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-client-licence-modal.component.html'
})
export class CreateClientLicenceModalComponent implements OnInit {
  private fb = inject(FormBuilder);
  private licenseService = inject(LicenseService);
  private projectService = inject(ProjectService);
  private cdr = inject(ChangeDetectorRef)

  @Input() clientId!: string;
  @Input() clientName!: string; // Nom du client passé par le parent
  @Output() close = new EventEmitter<void>();
  @Output() created = new EventEmitter<void>();

  licenseForm!: FormGroup;
  projects: Project[] = [];
  dynamicParameters: Parameter[] = [];

  // États de pagination
  page = 1;
  size = 10;
  isLastPage = false;

  isLoading = false;
  isLoadingProjects = false;

  ngOnInit() {
    this.initForm();
    this.loadProjects();
  }

  initForm() {
    this.licenseForm = this.fb.group({
      projectId: ['', Validators.required],
      dynamicParams: this.fb.group({})
    });

    this.licenseForm.get('projectId')?.valueChanges.subscribe(projectId => {
      this.generateDynamicFields(projectId);
    });
  }

  loadProjects() {
    if (this.isLastPage || this.isLoadingProjects) return;

    this.isLoadingProjects = true;
    this.projectService.fetchProjects("",this.page, this.size).subscribe({
      next: (response: any) => {
        // On ajoute les nouveaux projets à la liste existante
        this.projects = [...this.projects, ...response.content];
        this.isLastPage = response.last;
        this.isLoadingProjects = false;
        this.cdr.detectChanges();
      },
      error: () => this.isLoadingProjects = false
    });
  }

  fetchMoreProjects() {
    this.page++;
    this.loadProjects();
  }

  generateDynamicFields(projectId: string) {
    const selectedProject = this.projects.find(p => p.id === projectId);
    const dynamicGroup = this.licenseForm.get('dynamicParams') as FormGroup;

    // 1. Réinitialisation complète des valeurs et de l'état (touched/dirty) du groupe
    dynamicGroup.reset();

    // 2. Nettoyage des contrôles existants
    // On utilise Object.keys pour itérer sur les contrôles actuels et les supprimer un par un
    Object.keys(dynamicGroup.controls).forEach(key => {
      dynamicGroup.removeControl(key);
    });

    // 3. Réinitialisation de la liste des paramètres pour le template
    this.dynamicParameters = [];

    if (selectedProject?.licenseModel?.parameters) {
      this.dynamicParameters = selectedProject.licenseModel.parameters;

      this.dynamicParameters.forEach(param => {
        // 4. Injection des nouveaux contrôles vierges
        dynamicGroup.addControl(
          param.label,
          new FormControl('', Validators.required)
        );
      });
    }

    // 5. Forcer la détection de changement pour mettre à jour l'UI immédiatement
    this.cdr.detectChanges();
  }

  isProjectSelected(id: string): boolean {
    return this.licenseForm.get('projectId')?.value === id;
  }

  onSubmit() {
    if (this.licenseForm.invalid) return;
    this.isLoading = true;

    const dynamicValues = this.licenseForm.get('dynamicParams')?.value;

    // Conversion en List<LicenseParameterDto>
    const formattedParameters = this.dynamicParameters.map(param => ({
      label: param.label,
      value: String(dynamicValues[param.label]),
      type: param.type
    }));

    const payload = {
      clientId: this.clientId,
      projectId: this.licenseForm.value.projectId,
      parameters: formattedParameters // Format liste d'objets
    };

    this.licenseService.createLicense(payload).subscribe({
      next: () => {
        this.created.emit();
        this.close.emit();
      },
      error: () => this.isLoading = false
    });
  }
}
