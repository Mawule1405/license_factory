import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProjectService } from '../../../../../core/services/project.service';
import { NotificationService } from '../../../../../core/services/notification.service';
import { finalize } from 'rxjs';
import { Project } from '../../../../../core/models/project.model';

@Component({
  selector: 'app-edit-project-metadata-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './edit-project-metadata-modal.component.html'
})
export class EditProjectMetadataModalComponent implements OnInit {
  private fb = inject(FormBuilder);
  private projectService = inject(ProjectService);
  private notifyService = inject(NotificationService);

  @Input({ required: true }) project!: Project;
  @Output() close = new EventEmitter<void>();
  @Output() updated = new EventEmitter<void>();

  isLoading: boolean = false;

  projectForm: FormGroup = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    description: ['', [Validators.required]]
  });

  ngOnInit(): void {
    if (this.project) {
      this.projectForm.patchValue({
        name: this.project.name,
        description: this.project.description
      });
    }
  }

  onSubmit() {
    if (this.projectForm.valid && !this.isLoading) {
      this.isLoading = true;

      // On envoie uniquement les champs modifiés au service
      this.projectService.updateProject(this.project.id!, this.projectForm.value)
        .pipe(finalize(() => this.isLoading = false))
        .subscribe({
          next: () => {
            this.notifyService.success("Metadata updated successfully.", "UPDATED");
            this.updated.emit();
            this.close.emit();
          },
          error: (err) => console.error(err)
        });
    }
  }
}
