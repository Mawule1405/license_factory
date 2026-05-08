import {Component, inject} from '@angular/core';
import {Router, RouterLink} from '@angular/router';

@Component({
  selector: 'app-not-found',
  imports: [
    RouterLink
  ],
  templateUrl: './not-found.component.html',
  styleUrl: './not-found.component.css',
})
export class NotFoundComponent {
  private router = inject(Router);

  // Récupère le chemin qui a causé la 404
  get currentPath(): string {
    return this.router.url;
  }
}
