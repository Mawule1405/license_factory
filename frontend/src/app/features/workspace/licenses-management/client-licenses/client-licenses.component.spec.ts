import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientLicensesComponent } from './client-licenses.component';

describe('ClientLicensesComponent', () => {
  let component: ClientLicensesComponent;
  let fixture: ComponentFixture<ClientLicensesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientLicensesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClientLicensesComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
