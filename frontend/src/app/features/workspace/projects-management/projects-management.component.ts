import {ChangeDetectorRef, Component, inject, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import { RouterOutlet} from '@angular/router';
import {ClientUiService} from '../../../core/services/client-ui.service';
import {ProjectStats} from '../../../core/models/project.model';
import {ProjectService} from '../../../core/services/project.service';

@Component({
  selector: 'app-projects-management',
  standalone: true,
  imports: [CommonModule, FormsModule,  RouterOutlet],
  templateUrl: './projects-management.component.html'
})
export class ProjectsManagementComponent implements OnInit {

  private projectService = inject(ProjectService);
  private cdr = inject(ChangeDetectorRef)

  // Dans ta classe de composant
  miniStats: ProjectStats = {
    total: 0,
    totalThisMonth: 0,
    newDeployments: 0,
    lastDeployedName: 'WAITING_STREAM...',
    topLicensedProject: 'N/A',
    leadArchitect: 'N/A'
  };


  ngOnInit() {
    this.projectService.fetchProjectMiniStats().subscribe((data)=>
    {
      this.miniStats = data
      this.cdr.detectChanges()
    });
  }


}
