import {Component, OnInit, inject, ChangeDetectorRef} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';
import {UserService} from '../../../../core/services/user.service';
import {AppRole, AppUser} from '../../../../core/models/auth.model';
import {AppRoleService} from '../../../../core/services/app-role.service';

@Component({
  selector: 'app-rules',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './rules.component.html'
})
export class RulesComponent implements OnInit {
  private ruleService = inject(AppRoleService);
  private cdr = inject(ChangeDetectorRef)

  // Data
  rules: AppRole[] = [];
  isCreateModalOpen = false;

  // State Management
  loading = false;
  searchTerm = '';


  // Search Optimization
  private searchSubject = new Subject<string>();


  ngOnInit() {
    this.searchSubject.pipe(
      debounceTime(200),
      distinctUntilChanged()
    ).subscribe(() => {
      this.loadRules();
    });

    this.loadRules();
  }

  loadRules() {
    this.loading = true;
    this.ruleService.fetchAll()
      .subscribe({
        next: (response) => {
          this.rules = response;

          this.loading = false;
          this.cdr.detectChanges()
        },
        error: (err) => {
          console.error('Data stream interrupted', err);
          this.loading = false;
          this.cdr.detectChanges()
        }
      });
  }

}
