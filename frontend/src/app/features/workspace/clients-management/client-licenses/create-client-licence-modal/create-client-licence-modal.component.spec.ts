import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateClientLicenceModalComponent } from './create-client-licence-modal.component';

describe('CreateClientLicenceComponent', () => {
  let component: CreateClientLicenceModalComponent;
  let fixture: ComponentFixture<CreateClientLicenceModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateClientLicenceModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateClientLicenceModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
