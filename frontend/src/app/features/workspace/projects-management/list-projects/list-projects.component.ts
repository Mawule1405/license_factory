import {ChangeDetectorRef, Component, inject, OnInit} from '@angular/core';
import {ProjectService} from '../../../../core/services/project.service';
import {Project} from '../../../../core/models/project.model';
import {FormsModule} from '@angular/forms';
import {CreateProjectModalComponent} from './create-project-modal/create-project-modal.component';
import {PaginationComponent} from '../../../../shared/components/layout/pagination/pagination.component';
import {DatePipe} from '@angular/common';
import {EditLicenseModelModalComponent} from './edit-license-model-modal/edit-license-model-modal.component';
import {NotificationService} from '../../../../core/services/notification.service';
import {finalize} from 'rxjs';
import {EditProjectMetadataModalComponent} from './edit-project-metadata-modal/edit-project-metadata-modal.component';
import {
  CreateProjectLicenseModalComponent
} from './create-project-license-modal/create-project-license-modal.component';

@Component({
  selector: 'app-list-projects',
  imports: [
    FormsModule,
    CreateProjectModalComponent,
    PaginationComponent,
    DatePipe,
    EditLicenseModelModalComponent,
    EditProjectMetadataModalComponent,
    CreateProjectLicenseModalComponent,

  ],
  templateUrl: './list-projects.component.html',
  styleUrl: './list-projects.component.css',
})
export class ListProjectsComponent implements OnInit{
  private projectService = inject(ProjectService);
  private notifyService = inject(NotificationService);
  private cdr = inject(ChangeDetectorRef);

  projects: Project[] = [];
  loading = false;
  searchKey = '';
  isModalOpen = false;
  isEditLicenseModelOpen = false;
  isEditMetadataOpen = false
  isCreateLicenseOpen = false
  selectedProject!: Project;
  pagination = {
    totalPages: 0,
    totalElements: 0,
    page:1,
    size:10,
  };

  ngOnInit() {
    this.loadProjects();
  }

  loadProjects() {
    this.loading = true;
    this.projectService.fetchProjects(this.searchKey, this.pagination.page, this.pagination.size)
      .subscribe({
        next: (res) => {
          this.loading = false;
          this.projects = res.content;
          this.pagination.totalElements = res.totalElements;
          this.pagination.totalPages = res.totalPages;
          this.pagination.page = res.page;
          this.pagination.size = res.size;
          console.log(res.content);
          this.cdr.detectChanges();
        },
        error: () => {
          this.loading = false
          this.cdr.detectChanges();
        }
      });
  }

  onSearch() {
    this.pagination.page = 1
    this.loadProjects();
  }

  handleDelete(id: string) {
    this.notifyService.confirm(
      "Are you sure you want to terminate this project? This action will mark the project as deleted in the registry.",
      "TERMINATE_PROJECT"
    ).then((result) => {
      if (result) {
        // Activation du loader global ou local si tu en as un
        this.loading = true;

        this.projectService.deleteProject(id)
          .pipe(
            // On s'assure que le loading s'arrête quoi qu'il arrive
            finalize(() => this.loading = false)
          )
          .subscribe({
            next: () => {
              // Notification de succès
              this.notifyService.success(
                "Project successfully decommissioned.",
                "DELETED"
              );
              // Rechargement de la liste pour mettre à jour l'affichage et les KPIs
              this.loadProjects();
            },
            error: (err) => {
              // L'erreur est normalement gérée par ton Interceptor,
              // mais on peut ajouter un log spécifique ici.
              console.error("Deletion failed:", err);
            }
          });
      }
    });
  }


  handlePageChange(page: number) {
    this.pagination.page = page;
    this.loadProjects();
  }

  handleSizeChange(size: number) {
    this.pagination.size = size;
    this.pagination.page = 1; // Toujours revenir à la page 1
    this.loadProjects();
  }

  openLicenseModelModal(project: Project) {
    this.selectedProject = project;
    this.isEditLicenseModelOpen = true;

  }

  openCreateLicenseModal(project: Project) {
    this.selectedProject = project
    this.isCreateLicenseOpen = true;

  }

  openEditProjectModal(project: Project) {
    this.selectedProject = project
    this.isEditMetadataOpen = true;
  }
}
