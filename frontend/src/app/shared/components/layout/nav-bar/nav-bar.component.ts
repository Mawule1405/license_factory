import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {StorageService} from '../../../../core/services/storage.service';
import {AuthService} from '../../../../core/services/auth.service';
import {ACCESS_TOKEN} from '../../../../core/constants/auth.constants';
import {ClickOutsideDirective} from '../../../directives/click-outside.directive';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, ClickOutsideDirective],
  templateUrl: './nav-bar.component.html'
})
export class NavbarComponent {
  private storage = inject(StorageService);
  private authService = inject(AuthService);

  username = this.storage.getUsernameFromToken(ACCESS_TOKEN);
  isDropdownOpen = false;

  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  logout() {
    this.authService.logout();
  }
}
