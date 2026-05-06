import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import {LicenseParameter, LicenseResponse} from '../../../../../core/models/license.model';

@Component({
  selector: 'app-license-parameters-modal',
  standalone: true,
  imports: [CommonModule], // Plus besoin de ReactiveFormsModule !
  templateUrl: './license-parameters-modal.component.html'
})
export class LicenseParametersModalComponent implements OnInit {
  @Input({ required: true }) license!: LicenseResponse; // Typage fort
  @Output() close = new EventEmitter<void>();

  displayParameters: LicenseParameter[] = [];

  ngOnInit() {
    if (this.license && this.license.parameters) {
      this.displayParameters = this.license.parameters;
    }
  }
}
