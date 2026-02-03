import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JavaOnlineDocsComponent } from './java-online-docs.component';

describe('JavaOnlineDocsComponent', () => {
  let component: JavaOnlineDocsComponent;
  let fixture: ComponentFixture<JavaOnlineDocsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JavaOnlineDocsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JavaOnlineDocsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
