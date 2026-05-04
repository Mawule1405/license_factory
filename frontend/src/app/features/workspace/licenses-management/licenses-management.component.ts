import {ChangeDetectorRef, Component, inject, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { ClientUiService } from '../../../core/services/client-ui.service';
import { LicenseStats } from '../../../core/models/license.model'; // Import du nouveau modèle
import { LicenseService } from '../../../core/services/license.service'; // Service à injecter

@Component({
  selector: 'app-licenses-management',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  templateUrl: './licenses-management.component.html'
})
export class LicensesManagementComponent implements OnInit {
  private licenseService = inject(LicenseService);
  private cdr = inject(ChangeDetectorRef)



  // Remplacement de miniStats par le type LicenseStats
  miniStats: LicenseStats = {
    total: 0,
    activeTotal: 0,
    growthRate: 0,
    conversionEfficiency: 0,
    lastDeployedName: 'WAITING_STREAM...',
    topLicensedProject: 'N/A',
    leadArchitect: 'N/A',
    deploymentDensity: 0
  };

  ngOnInit() {
    this.loadLicenseStats();
  }

  loadLicenseStats() {

    this.licenseService.fetchLicenseMiniStats().subscribe(stats => {
      this.miniStats = stats
      this.cdr.detectChanges();
    });
  }
}
