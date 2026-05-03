import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExportLogsComponent } from './export-logs.component';

describe('ExportLogsComponent', () => {
  let component: ExportLogsComponent;
  let fixture: ComponentFixture<ExportLogsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExportLogsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExportLogsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
