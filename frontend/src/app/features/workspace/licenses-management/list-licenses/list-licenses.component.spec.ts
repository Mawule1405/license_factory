import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListLicensesComponent } from './list-licenses.component';

describe('ListClientsComponent', () => {
  let component: ListLicensesComponent;
  let fixture: ComponentFixture<ListLicensesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListLicensesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListLicensesComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
