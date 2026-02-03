import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PythonOfflineDocsComponent } from './python-offline-docs.component';

describe('PythonOfflineDocsComponent', () => {
  let component: PythonOfflineDocsComponent;
  let fixture: ComponentFixture<PythonOfflineDocsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PythonOfflineDocsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PythonOfflineDocsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
