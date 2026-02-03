import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { NotificationConfig, NotificationType } from '../models/notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationService {

  private notificationSubject = new Subject<NotificationConfig | null>();
  notificationState$ = this.notificationSubject.asObservable();

  private resolveCallback?: (value: boolean) => void;

  /**
   * Méthode générique pour afficher une notification.
   * Retourne une Promise pour gérer facilement les confirmations (Permission/Warning).
   */
  notify(config: NotificationConfig): Promise<boolean> {
    this.notificationSubject.next(config);
    return new Promise((resolve) => {
      this.resolveCallback = resolve;
    });
  }

  // Helpers rapides
  success(message: string, title = 'SUCCESS_COMMIT') {
    this.notify({ type: 'SUCCESS', title, message });
  }

  error(message: string, title = 'SYSTEM_FAILURE') {
    this.notify({ type: 'ERROR', title, message });
  }

  confirm(message: string, title = 'SECURITY_CLEARANCE'): Promise<boolean> {
    return this.notify({
      type: 'PERMISSION',
      title,
      message,
      confirmText: 'PROCEED',
      cancelText: 'ABORT'
    });
  }

  close(result: boolean) {
    this.notificationSubject.next(null);
    if (this.resolveCallback) this.resolveCallback(result);
  }
}
