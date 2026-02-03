import { Component } from '@angular/core';
import {NavbarComponent} from '../../shared/components/layout/nav-bar/nav-bar.component';
import {RouterOutlet} from '@angular/router';
import {APP_VERSION} from '../../core/constants/app-info.constants';

@Component({
  selector: 'app-workspace.component',
  imports: [
    NavbarComponent,
    RouterOutlet
  ],
  templateUrl: './workspace.component.html',
  styleUrl: './workspace.component.css',
})
export class WorkspaceComponent {

  protected readonly APP_VERSION = APP_VERSION;
}
