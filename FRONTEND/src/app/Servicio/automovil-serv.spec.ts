import { TestBed } from '@angular/core/testing';

import { AutomovilServ } from './automovil-serv';

describe('AutomovilServ', () => {
  let service: AutomovilServ;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AutomovilServ);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
