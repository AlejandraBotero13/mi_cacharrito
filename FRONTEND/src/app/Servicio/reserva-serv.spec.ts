import { TestBed } from '@angular/core/testing';

import { ReservaServ } from './reserva-serv';

describe('ReservaServ', () => {
  let service: ReservaServ;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReservaServ);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
