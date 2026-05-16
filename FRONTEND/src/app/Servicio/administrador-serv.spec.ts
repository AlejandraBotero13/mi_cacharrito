import { TestBed } from '@angular/core/testing';

import { AdministradorServ } from './administrador-serv';

describe('AdministradorServ', () => {
  let service: AdministradorServ;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdministradorServ);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
