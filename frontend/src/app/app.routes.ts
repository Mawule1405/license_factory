import { Routes } from '@angular/router';
import {LoginComponent} from './features/login/login.component';
import {WorkspaceComponent} from './features/workspace/workspace.component';
import {DashboardComponent} from './features/workspace/dashboard/dashboard.component';
import {AdministrationComponent} from './features/workspace/administration/administration.component';
import {authGuard} from './core/guards/auth.guard';
import {ClientsManagementComponent} from './features/workspace/clients-management/clients-management.component';
import {DocumentationsComponent} from './features/workspace/documentations/documentations.component';
import {ProfileComponent} from './features/workspace/profile/profile.component';
import {ChangePasswordComponent} from './features/workspace/change-password/change-password.component';
import {UsersComponent} from './features/workspace/administration/users/users.component';
import {LogsComponent} from './features/workspace/administration/logs/logs.component';
import {SettingsComponent} from './features/workspace/administration/settings/settings.component';
import {adminGuard} from './core/guards/admin.guard';
import {ListClientsComponent} from './features/workspace/clients-management/list-clients/list-clients.component';
import {
  ClientLicensesComponent
} from './features/workspace/clients-management/client-licenses/client-licenses.component';
import {OnlineComponent} from './features/workspace/documentations/online/online.component';
import {OfflineComponent} from './features/workspace/documentations/offline/offline.component';
import {AboutComponent} from './features/workspace/about/about.component';
import {ArchitectureComponent} from './features/workspace/documentations/architecture/architecture.component';
import {SecurityComponent} from './features/workspace/documentations/security/security.component';

export const routes: Routes = [
  {path:'', redirectTo:'login', pathMatch: 'full'},
  {path:'login', component: LoginComponent},
  {path:'workspace', component: WorkspaceComponent, children:[
      {path: '', redirectTo:'dashboard', pathMatch: 'full'},
      {path: 'dashboard', component: DashboardComponent},
      {path:'administration', component: AdministrationComponent, canActivate:[authGuard, adminGuard],
      children:[
        {path:'', redirectTo:'users', pathMatch: 'full'},
        {path:'users', component: UsersComponent, canActivate:[authGuard, adminGuard],},
        {path:'logs', component: LogsComponent,canActivate:[authGuard, adminGuard]},
        {path:'settings',component: SettingsComponent,canActivate:[authGuard, adminGuard]},
      ]},
      {path: 'clients', component: ClientsManagementComponent, canActivate:[authGuard],
        children: [
          { path: '', component: ListClientsComponent }, // La liste globale
          { path: ':clientId/licenses', component: ClientLicensesComponent }, // Les licences d'un client
        ]
      },
      {path: 'docs', component: DocumentationsComponent, canActivate:[authGuard],children:[
          { path: '', redirectTo:'online', pathMatch: 'full'},
          {path:'online', component: OnlineComponent},
          {path:'offline', component: OfflineComponent},
          {path:'architecture', component: ArchitectureComponent},
          {path:'security', component: SecurityComponent},
        ]},
      {path: 'profile', component: ProfileComponent, canActivate:[authGuard]},
      {path: 'about', component: AboutComponent, canActivate:[authGuard]},
      {path: 'change-password', component: ChangePasswordComponent, canActivate:[authGuard]},
    ]},

  {path: '**', redirectTo: 'dashboard', pathMatch: 'full' },
];
