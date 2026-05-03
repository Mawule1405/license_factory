import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateLicenseModalComponent } from './create-license-modal.component';

describe('CreateClientModalComponent', () => {
  let component: CreateLicenseModalComponent;
  let fixture: ComponentFixture<CreateLicenseModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateLicenseModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateLicenseModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
