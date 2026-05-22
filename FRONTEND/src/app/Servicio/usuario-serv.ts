import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Usuario } from '../Usuario/usuarios/usuarios';

@Injectable({
  providedIn: 'root'
})
export class UsuarioServ {

  private url = 'http://localhost:8080/usuarios/u';

  constructor(private http: HttpClient) {}

  listarUsuarios(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${this.url}/listar`);
  }

  buscarPorCc(cc: string): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.url}/buscarCc`, { params: { cc } });
  }

  guardarUsuario(usuario: Usuario): Observable<Usuario> {
    return this.http.post<Usuario>(`${this.url}/guardar`, usuario);
  }

  eliminarUsuario(cc: string): Observable<string> {
    return this.http.delete(`${this.url}/eliminar`, { params: { cc }, responseType: 'text' });
  }
}