import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateProjectLicenseModalComponent } from './create-project-license-modal.component';

describe('CreateClientModalComponent', () => {
  let component: CreateProjectLicenseModalComponent;
  let fixture: ComponentFixture<CreateProjectLicenseModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateProjectLicenseModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateProjectLicenseModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
