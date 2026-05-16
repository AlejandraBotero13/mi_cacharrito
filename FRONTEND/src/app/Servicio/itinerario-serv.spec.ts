import { TestBed } from '@angular/core/testing';

import { ItinerarioServ } from './itinerario-serv';

describe('ItinerarioServ', () => {
  let service: ItinerarioServ;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ItinerarioServ);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
