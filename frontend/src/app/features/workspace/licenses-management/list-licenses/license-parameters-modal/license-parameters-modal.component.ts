import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-license-parameters-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './license-parameters-modal.component.html'
})
export class LicenseParametersModalComponent implements OnInit {
  private fb = inject(FormBuilder);

  @Input({ required: true }) license!: any; // La licence sélectionnée à afficher
  @Output() close = new EventEmitter<void>();

  parameterForm!: FormGroup;
  parameterKeys: string[] = [];

  ngOnInit() {
    this.initDisplayForm();
  }

  initDisplayForm() {
    // On extrait les clés des paramètres pour l'affichage
    if (this.license && this.license.parameters) {
      this.parameterKeys = Object.keys(this.license.parameters);

      const group: any = {};
      this.parameterKeys.forEach(key => {
        // On initialise chaque champ avec sa valeur et on le désactive (lecture seule)
        group[key] = [{ value: this.license.parameters[key], disabled: true }];
      });

      this.parameterForm = this.fb.group(group);
    }
  }
}
