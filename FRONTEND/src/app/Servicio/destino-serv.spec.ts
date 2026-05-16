import { TestBed } from '@angular/core/testing';

import { DestinoServ } from './destino-serv';

describe('DestinoServ', () => {
  let service: DestinoServ;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DestinoServ);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
