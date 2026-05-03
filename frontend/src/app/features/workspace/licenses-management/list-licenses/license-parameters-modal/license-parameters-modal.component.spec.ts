import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LicenseParametersModalComponent } from './license-parameters-modal.component';

describe('CreateClientModalComponent', () => {
  let component: LicenseParametersModalComponent;
  let fixture: ComponentFixture<LicenseParametersModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LicenseParametersModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LicenseParametersModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
