import { TestBed } from '@angular/core/testing';

import { UsuarioServ } from './usuario-serv';

describe('UsuarioServ', () => {
  let service: UsuarioServ;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UsuarioServ);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
