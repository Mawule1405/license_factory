import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {
  NotificationContainerComponent
} from './shared/components/dialogs/notification-container/notification-container.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NotificationContainerComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  }
