import { Component } from '@angular/core';
import {NgClass} from '@angular/common';
import {JavaOfflineDocsComponent} from './java-offline-docs/java-offline-docs.component';
import {PythonOfflineDocsComponent} from './python-offline-docs/python-offline-docs.component';

@Component({
  selector: 'app-offline',
  imports: [
    NgClass,
    JavaOfflineDocsComponent,
    PythonOfflineDocsComponent
  ],
  templateUrl: './offline.component.html',
  styleUrl: './offline.component.css',
})
export class OfflineComponent {
  selectedLang="java"
}
