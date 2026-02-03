import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ClientUiService {
  private clientNameSource = new BehaviorSubject<string>('');
  currentClientName$ = this.clientNameSource.asObservable();

  setClientName(name: string) {
    this.clientNameSource.next(name);
  }
}
