import {Component, inject, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ClientService} from '../../../core/services/client.service';
import {Client} from '../../../core/models/client.model';
import {CreateClientModalComponent} from './list-clients/create-client-modal/create-client-modal.component';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {ClientUiService} from '../../../core/services/client-ui.service';

@Component({
  selector: 'app-clients-management',
  standalone: true,
  imports: [CommonModule, FormsModule,  RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './clients-management.component.html'
})
export class ClientsManagementComponent implements OnInit {

  private uiService = inject(ClientUiService);
  activeClientName = '';

  ngOnInit() {
    this.uiService.currentClientName$.subscribe(name => this.activeClientName = name);
  }
}
