import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditLicenseModelModalComponent } from './edit-license-model-modal.component';

describe('CreateClientModalComponent', () => {
  let component: EditLicenseModelModalComponent;
  let fixture: ComponentFixture<EditLicenseModelModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditLicenseModelModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditLicenseModelModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
