import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectsManagementComponent } from './projects-management.component';

describe('ClientsManagementComponent', () => {
  let component: ProjectsManagementComponent;
  let fixture: ComponentFixture<ProjectsManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectsManagementComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProjectsManagementComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
