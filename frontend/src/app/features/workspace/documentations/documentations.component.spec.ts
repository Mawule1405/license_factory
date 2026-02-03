import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentationsComponent } from './documentations.component';

describe('DocumentationsComponent', () => {
  let component: DocumentationsComponent;
  let fixture: ComponentFixture<DocumentationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DocumentationsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DocumentationsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
