import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JavaOfflineDocsComponent } from './java-offline-docs.component';

describe('JavaOfflineDocsComponent', () => {
  let component: JavaOfflineDocsComponent;
  let fixture: ComponentFixture<JavaOfflineDocsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JavaOfflineDocsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JavaOfflineDocsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
