import { Component } from '@angular/core';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-documentations.component',
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './documentations.component.html',
  styleUrl: './documentations.component.css',
})
export class DocumentationsComponent {

}
