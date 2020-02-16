import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HsmLoginComponent } from './hsm-login.component';

describe('HsmLoginComponent', () => {
  let component: HsmLoginComponent;
  let fixture: ComponentFixture<HsmLoginComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HsmLoginComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HsmLoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
