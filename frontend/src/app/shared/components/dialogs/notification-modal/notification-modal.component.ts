import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import {NotificationConfig} from '../../../../core/models/notification.model';

@Component({
  selector: 'app-notification-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification-modal.component.html',

})
export class NotificationModalComponent {
  @Input() config!: NotificationConfig;
  @Output() confirmed = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  getStyles() {
    switch (this.config.type) {
      case 'SUCCESS': return { icon: 'fa-check-double', color: 'text-taurus-green', border: 'border-taurus-green', bg: 'bg-taurus-green' };
      case 'ERROR': return { icon: 'fa-exclamation-triangle', color: 'text-red-600', border: 'border-red-600', bg: 'bg-red-600' };
      case 'WARNING': return { icon: 'fa-radiation', color: 'text-amber-500', border: 'border-amber-500', bg: 'bg-amber-500' };
      case 'PERMISSION': return { icon: 'fa-user-shield', color: 'text-taurus-green', border: 'border-green-900', bg: 'bg-taurus-green' };
      default: return { icon: 'fa-info-circle', color: 'text-gray-600', border: 'border-gray-600', bg: 'bg-gray-600' };
    }
  }
}
