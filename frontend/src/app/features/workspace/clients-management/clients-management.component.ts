import {ChangeDetectorRef, Component, inject, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ClientService} from '../../../core/services/client.service';
import {Client, ClientStats} from '../../../core/models/client.model';
import {CreateClientModalComponent} from './list-clients/create-client-modal/create-client-modal.component';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {ClientUiService} from '../../../core/services/client-ui.service';
import {ProjectStats} from '../../../core/models/project.model';

@Component({
  selector: 'app-clients-management',
  standalone: true,
  imports: [CommonModule, FormsModule,  RouterOutlet],
  templateUrl: './clients-management.component.html'
})
export class ClientsManagementComponent implements OnInit {

  clientService = inject(ClientService)
  private cdr = inject(ChangeDetectorRef)

  miniStats: ClientStats = {
    // --- Volume & Croissance ---
    total: 0,
    totalThisMonth: 0,
    growthRate: 0,           // Pourcentage de croissance vs mois dernier

    // --- Activité Technique ---
    activeDeployments: 0,    // Nombre de licences actuellement valides (non expirées)
    deploymentDensity: 0,    // Moyenne de projets par client (ex: 1.5 projets/client)

    // --- Records & Flux ---
    lastDeployedName: 'WAITING_STREAM...',
    topLicensedProject: 'N/A',

    // --- Performance Équipe ---
    leadArchitect: 'N/A',
    conversionEfficiency: 0  // Ratio clients avec licence / clients total (%)
  };

  ngOnInit() {
    this.clientService.fetchClientMiniStats().subscribe((data)=>{
      this.miniStats = data
      this.cdr.detectChanges();
    });
  }
}
