import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BotBarComponent } from './bot-bar.component';

describe('BotBarComponent', () => {
  let component: BotBarComponent;
  let fixture: ComponentFixture<BotBarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BotBarComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BotBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
