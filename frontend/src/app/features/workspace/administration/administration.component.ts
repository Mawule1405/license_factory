import { Component } from '@angular/core';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {APP_VERSION} from '../../../core/constants/app-info.constants';

@Component({
  selector: 'app-administration.component',
  imports: [
    RouterLink,
    RouterLinkActive,
    RouterOutlet
  ],
  templateUrl: './administration.component.html',
  styleUrl: './administration.component.css',
})
export class AdministrationComponent {

  protected readonly APP_VERSION = APP_VERSION;
}
