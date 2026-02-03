import { Component } from '@angular/core';
import {NgClass} from '@angular/common';
import {JavaOnlineDocsComponent} from './java-online-docs/java-online-docs.component';
import {PythonOnlineDocsComponent} from './python-online-docs/python-online-docs.component';

@Component({
  selector: 'app-online',
  imports: [
    NgClass,
    JavaOnlineDocsComponent,
    PythonOnlineDocsComponent
  ],
  templateUrl: './online.component.html',
  styleUrl: './online.component.css',
})
export class OnlineComponent {
  selectedLang= "java"
}
