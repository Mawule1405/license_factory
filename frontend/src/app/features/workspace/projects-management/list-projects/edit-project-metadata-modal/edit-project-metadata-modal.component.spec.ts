import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditProjectMetadataModalComponent } from './edit-project-metadata-modal.component';

describe('CreateClientModalComponent', () => {
  let component: EditProjectMetadataModalComponent;
  let fixture: ComponentFixture<EditProjectMetadataModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditProjectMetadataModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditProjectMetadataModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
