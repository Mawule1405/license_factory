import {Component, inject, signal} from '@angular/core';
import {AuditService} from '../../../../core/services/audit.service';

@Component({
  selector: 'app-logs.component',
  imports: [],
  templateUrl: './logs.component.html',
  styleUrl: './logs.component.css',
})
export class LogsComponent {
  private auditService = inject(AuditService);


  // État : quel type de log est affiché ?
  activeTab = signal<'technical' | 'audit'>('technical');
  isLoading = signal(false);

  // Les logs sont récupérés depuis le service
  logs = signal<string[]>([]);

  ngOnInit() {
    this.loadLogs();
  }

  // Basculer entre les onglets
  switchTab(tab: 'technical' | 'audit') {
    this.activeTab.set(tab);
    this.loadLogs();
  }

  loadLogs() {
    this.isLoading.set(true);
    this.auditService.getLogs(this.activeTab(), 100).subscribe({
      next: (data) => {
        this.logs.set(data);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  // Utilitaire pour colorer les lignes selon le contenu
  getLineClass(line: string): string {
    if (line.includes('ERROR')) return 'text-red-500 font-bold';
    if (line.includes('WARN')) return 'text-yellow-400';
    if (line.includes('|')) return 'text-taurus-green italic'; // Format spécifique à ton audit.log
    return 'text-gray-300';
  }
}
