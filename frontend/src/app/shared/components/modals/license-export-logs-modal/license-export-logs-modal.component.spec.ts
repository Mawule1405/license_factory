import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LicenseExportLogsModalComponent } from './license-export-logs-modal.component';

describe('CreateClientModalComponent', () => {
  let component: LicenseExportLogsModalComponent;
  let fixture: ComponentFixture<LicenseExportLogsModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LicenseExportLogsModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LicenseExportLogsModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
