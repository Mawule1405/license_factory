import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExportLicenseRaisonModalComponent } from './export-license-raison-modal.component';

describe('CreateClientModalComponent', () => {
  let component: ExportLicenseRaisonModalComponent;
  let fixture: ComponentFixture<ExportLicenseRaisonModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExportLicenseRaisonModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExportLicenseRaisonModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
