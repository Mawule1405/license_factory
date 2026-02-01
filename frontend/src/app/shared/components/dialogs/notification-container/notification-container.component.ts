import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {NotificationModalComponent} from '../notification-modal/notification-modal.component';
import {NotificationService} from '../../../../core/services/notification.service';

@Component({
  selector: 'app-notification-container',
  standalone: true,
  imports: [CommonModule, NotificationModalComponent],
  template: `
    @if (config$ | async ; as configData) {
      <app-notification-modal
        [config]="configData"
        (confirmed)="srv.close(true)"
        (cancelled)="srv.close(false)">
      </app-notification-modal>
    }
  `
})
export class NotificationContainerComponent {
  srv = inject(NotificationService);
  config$ = this.srv.notificationState$;

}
