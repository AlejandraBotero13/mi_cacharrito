import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Automoviles } from './automoviles';

describe('Automoviles', () => {
  let component: Automoviles;
  let fixture: ComponentFixture<Automoviles>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Automoviles]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Automoviles);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
