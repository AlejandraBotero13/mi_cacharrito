import { TestBed } from '@angular/core/testing';

import { ViajeServ } from './viaje-serv';

describe('ViajeServ', () => {
  let service: ViajeServ;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ViajeServ);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
