import { Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormArray } from '@angular/forms';
import { ProjectService } from '../../../../../core/services/project.service';
import { NotificationService } from '../../../../../core/services/notification.service';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-create-project-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-project-modal.component.html'
})
export class CreateProjectModalComponent {
  private fb = inject(FormBuilder);
  private projectService = inject(ProjectService);
  private notifyService = inject(NotificationService);

  @Output() close = new EventEmitter<void>();
  @Output() created = new EventEmitter<void>();

  isLoading: boolean = false;

  // Structure correspondant au ProjectRequest du Backend
  projectForm: FormGroup = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    description: ['', [Validators.required]],
    licenseModel: this.fb.group({
        parameters: this.fb.array([
          this.fb.group({
            label: ['MAC_ADDRESS', Validators.required],
            type: ['text', Validators.required]
          })
        ]),

    })
  });

  // Getter pour manipuler les paramètres de licence
  get parameters() {
    return this.projectForm.get('licenseModel.parameters') as FormArray;
  }

  addParameter() {
    // On ajoute un nouveau groupe avec les deux champs
    this.parameters.push(this.fb.group({
      label: ['', Validators.required],
      type: ['text', Validators.required]
    }));
  }

  removeParameter(index: number) {
    if (this.parameters.length > 1) {
      this.parameters.removeAt(index);
    }
  }

  onSubmit() {
    if (this.projectForm.valid && !this.isLoading) {
      this.isLoading = true; // Sécurité contre les doubles clics (Race Condition)

      this.projectService.createProject(this.projectForm.value)
        .pipe(finalize(() => this.isLoading = false))
        .subscribe({
          next: () => {
            this.notifyService.success("Project successfully created.", "SUCCESS");
            this.created.emit();
            this.close.emit();
          },
          error: (err) => {

            console.error(err);
          }
        });
    }
  }
}
