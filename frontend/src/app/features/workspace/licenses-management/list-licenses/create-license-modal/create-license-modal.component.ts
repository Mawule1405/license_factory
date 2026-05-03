import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormControl } from '@angular/forms';
import { ClientService } from '../../../../../core/services/client.service';
import { ProjectService } from '../../../../../core/services/project.service';
import { LicenseService } from '../../../../../core/services/license.service';
import { NotificationService } from '../../../../../core/services/notification.service';
import { Pagination } from '../../../../../core/models/auth.model';
import { Project } from '../../../../../core/models/project.model';
import {ChangeDetectorRef, Component, EventEmitter, inject, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-create-license-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-license-modal.component.html'
})
export class CreateLicenseModalComponent implements OnInit {
  private fb = inject(FormBuilder);
  private clientService = inject(ClientService);
  private projectService = inject(ProjectService);
  private licenseService = inject(LicenseService);
  private notifyService = inject(NotificationService);
  private cdr = inject(ChangeDetectorRef);

  @Output() close = new EventEmitter<void>();
  @Output() created = new EventEmitter<void>();

  // Formulaire principal
  licenseForm!: FormGroup;
  dynamicParamKeys: string[] = [];

  // Listes de données
  clients: any[] = [];
  projects: Project[] = [];

  // États de pagination
  clientPage = 1;
  projectPage = 1;
  pageSize = 10;
  hasMoreClients = true;
  hasMoreProjects = true;

  // États de chargement
  isLoading = false;
  loadingClients = false;
  loadingProjects = false;

  ngOnInit() {
    this.initForm();
    this.loadNextClients();
    this.loadNextProjects();
  }

  initForm() {
    this.licenseForm = this.fb.group({
      clientId: ['', [Validators.required]],
      projectId: ['', [Validators.required]],
      dynamicParams: this.fb.group({})
    });

    // Écouteur pour injecter les paramètres dès qu'un projet est choisi
    this.licenseForm.get('projectId')?.valueChanges.subscribe(projectId => {
      this.generateDynamicFields(projectId);
    });
  }

  loadNextClients() {
    if (this.loadingClients || !this.hasMoreClients) return;
    this.loadingClients = true;
    this.clientService.fetchClients(this.clientPage, this.pageSize, "").subscribe({
      next: (res: Pagination<any>) => {
        this.clients = [...this.clients, ...res.content];
        this.hasMoreClients = this.clientPage < res.totalPages;
        this.clientPage++;
        this.loadingClients = false;
        this.cdr.detectChanges();
      },
      error: () => this.loadingClients = false
    });
  }

  loadNextProjects() {
    if (this.loadingProjects || !this.hasMoreProjects) return;
    this.loadingProjects = true;
    this.projectService.fetchProjects("", this.projectPage, this.pageSize).subscribe({
      next: (res: Pagination<Project>) => {
        this.projects = [...this.projects, ...res.content];
        this.hasMoreProjects = this.projectPage < res.totalPages;
        this.projectPage++;
        this.loadingProjects = false;
        this.cdr.detectChanges();
      },
      error: () => this.loadingProjects = false
    });
  }

  generateDynamicFields(projectId: string) {
    const selectedProject = this.projects.find(p => p.id === projectId);
    const dynamicGroup = this.licenseForm.get('dynamicParams') as FormGroup;

    // Reset des contrôles existants
    Object.keys(dynamicGroup.controls).forEach(key => dynamicGroup.removeControl(key));
    this.dynamicParamKeys = [];

    if (selectedProject?.licenseModel?.parameters) {
      this.dynamicParamKeys = selectedProject.licenseModel.parameters;
      this.dynamicParamKeys.forEach(key => {
        dynamicGroup.addControl(key, new FormControl('', Validators.required));
      });
    }
  }

  onScroll(event: any, type: 'client' | 'project') {
    const element = event.target;
    if (element.scrollHeight - element.scrollTop <= element.clientHeight + 1) {
      type === 'client' ? this.loadNextClients() : this.loadNextProjects();
    }
  }

  isProjectSelected(id: string): boolean {
    return this.licenseForm.get('projectId')?.value === id;
  }

  onSubmit() {
    if (this.licenseForm.invalid) return;
    this.isLoading = true;

    const payload = {
      clientId: this.licenseForm.value.clientId,
      projectId: this.licenseForm.value.projectId,
      parameters: this.licenseForm.value.dynamicParams
    };

    this.licenseService.createLicense(payload).subscribe({
      next: () => {
        this.notifyService.success('LICENSE_GENERATED_SUCCESSFULLY');
        this.created.emit();
        this.close.emit();
      },
      error: () => {
        this.notifyService.error('ERROR_DURING_GENERATION');
        this.isLoading = false;
      }
    });
  }
}
