import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Itinerarios } from './itinerarios';

describe('Itinerarios', () => {
  let component: Itinerarios;
  let fixture: ComponentFixture<Itinerarios>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Itinerarios]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Itinerarios);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
