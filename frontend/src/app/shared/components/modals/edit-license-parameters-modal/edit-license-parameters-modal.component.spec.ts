import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditLicenseParametersModalComponent } from './edit-license-parameters-modal.component';

describe('CreateClientLicenceComponent', () => {
  let component: EditLicenseParametersModalComponent;
  let fixture: ComponentFixture<EditLicenseParametersModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditLicenseParametersModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditLicenseParametersModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
