
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UsuarioEnt } from '../Entidad/usuario-ent';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UsuarioServ {

  private url = `${environment.apiUrl}/usuarios/u`;

  constructor(private http: HttpClient) {}

  listarUsuarios(): Observable<UsuarioEnt[]> {
    return this.http.get<UsuarioEnt[]>(`${this.url}/listar`);
  }

  buscarPorCc(cc: string): Observable<UsuarioEnt> {
    return this.http.get<UsuarioEnt>(`${this.url}/buscarCc`, { params: { cc } });
  }

  guardarUsuario(usuario: UsuarioEnt): Observable<UsuarioEnt> {
    return this.http.post<UsuarioEnt>(`${this.url}/guardar`, usuario);
  }

  eliminarUsuario(cc: string): Observable<string> {
    return this.http.delete(`${this.url}/eliminar`, { params: { cc }, responseType: 'text' });
  }
}

