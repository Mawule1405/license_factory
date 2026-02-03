import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PythonOnlineDocsComponent } from './python-online-docs.component';

describe('PythonOnlineDocsComponent', () => {
  let component: PythonOnlineDocsComponent;
  let fixture: ComponentFixture<PythonOnlineDocsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PythonOnlineDocsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PythonOnlineDocsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
