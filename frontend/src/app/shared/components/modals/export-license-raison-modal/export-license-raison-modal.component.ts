import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-export-license-raison-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './export-license-raison-modal.component.html'
})
export class ExportLicenseRaisonModalComponent implements OnInit {
  private fb = inject(FormBuilder);

  @Input({ required: true }) licenseId!: string;
  @Output() close = new EventEmitter<void>();
  @Output() confirmed = new EventEmitter<{raison: string}>();

  exportForm!: FormGroup;
  isLoading = false;

  ngOnInit() {
    this.exportForm = this.fb.group({
      raison: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(255)]]
    });
  }

  onSubmit() {
    if (this.exportForm.valid) {
      this.isLoading = true;
      // On émet la raison au composant parent qui gérera l'appel API d'exportation
      this.confirmed.emit(this.exportForm.value);
    }
  }
}
