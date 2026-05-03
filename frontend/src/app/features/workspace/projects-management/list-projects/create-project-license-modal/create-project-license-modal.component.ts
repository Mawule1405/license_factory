import {Component, EventEmitter, Input, OnInit, OnDestroy, Output, inject, ChangeDetectorRef} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormControl } from '@angular/forms';
import { Subject, takeUntil, finalize } from 'rxjs';

import { LicenseService } from '../../../../../core/services/license.service';
import { ClientService } from '../../../../../core/services/client.service';
import { NotificationService } from '../../../../../core/services/notification.service';
import { Project } from '../../../../../core/models/project.model';
import { Client } from '../../../../../core/models/client.model';

@Component({
  selector: 'app-create-project-license-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-project-license-modal.component.html',
  styleUrls: ['./create-project-license-modal.component.css'] // Optionnel pour le scrollbar
})
export class CreateProjectLicenseModalComponent implements OnInit, OnDestroy {
  private fb = inject(FormBuilder);
  private licenseService = inject(LicenseService);
  private clientService = inject(ClientService);
  private notify = inject(NotificationService);
  private cdr = inject(ChangeDetectorRef)

  @Input({ required: true }) project!: Project;
  @Output() close = new EventEmitter<void>();
  @Output() created = new EventEmitter<void>();

  private destroy$ = new Subject<void>();

  // Formulaire & Data
  licenseForm: FormGroup;
  clients: Client[] = [];

  // États UI
  isLoading = false;
  isLoadingClients = false;

  // Pagination
  currentPage = 0;
  pageSize = 5; // Petit pas pour tester la pagination facilement
  isLastPage = false;

  constructor() {
    this.licenseForm = this.fb.group({
      clientId: ['', Validators.required],
      dynamicParams: this.fb.group({})
    });
  }

  ngOnInit(): void {
    this.fetchMoreClients();
    this.injectDynamicControls();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Charge la page suivante de clients et les ajoute à la liste existante
   */
  fetchMoreClients(): void {
    if (this.isLastPage || this.isLoadingClients) return;

    this.isLoadingClients = true;
    this.clientService.fetchClients(this.currentPage+1, this.pageSize, "")
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => this.isLoadingClients = false)
      )
      .subscribe({
        next: (res) => {
          this.clients = [...this.clients, ...res.content];
          this.isLastPage = res.totalPages===res.page;
          this.currentPage++;
          this.cdr.detectChanges();
        },
        error: () => this.notify.error("Registry_Sync_Failure", "NETWORK_ERROR")
      });
  }

  /**
   * Construit dynamiquement les champs du formulaire basés sur le modèle du projet
   */
  private injectDynamicControls(): void {
    const dynamicGroup = this.licenseForm.get('dynamicParams') as FormGroup;

    if (this.project?.licenseModel?.parameters) {
      this.project.licenseModel.parameters.forEach(param => {
        dynamicGroup.addControl(param, new FormControl('', Validators.required));
      });
    }
  }

  /**
   * Helpers pour le template
   */
  get dynamicParamKeys(): string[] {
    return Object.keys((this.licenseForm.get('dynamicParams') as FormGroup).controls);
  }

  isClientSelected(id: string|undefined): boolean {
    return this.licenseForm.get('clientId')?.value === id;
  }

  /**
   * Soumission finale
   */
  onSubmit(): void {
    if (this.licenseForm.invalid) {
      this.licenseForm.markAllAsTouched();
      //this.notify.warn("Incomplete data fields", "VALIDATION_FAILED");
      return;
    }

    this.isLoading = true;
    const payload = {
      clientId: this.licenseForm.value.clientId,
      projectId: this.project.id,
      parameters: this.licenseForm.value.dynamicParams
    };

    this.licenseService.createLicense(payload)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: () => {
          this.notify.success(`License Forge Successful: ${this.project.name}`, "COMMIT_OK");
          this.created.emit();
          this.close.emit();
        },
        error: (err) => {
          this.notify.error("Provisioning failed. Check server logs.", "EXECUTION_ERROR");
        }
      });
  }
}
