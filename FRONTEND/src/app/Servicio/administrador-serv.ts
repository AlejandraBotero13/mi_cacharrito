import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AdministradorEnt } from '../Entidad/administrador-ent';

@Injectable({
  providedIn: 'root'
})
export class AdministradorServ {

  private url = 'http://localhost:8080/administradores/a';

  constructor(private http: HttpClient) { }

  listar(): Observable<AdministradorEnt[]> {
    return this.http.get<AdministradorEnt[]>(`${this.url}/listar`);
  }

  buscarPorId(id: number): Observable<AdministradorEnt> {
    return this.http.get<AdministradorEnt>(`${this.url}/buscarId`, { params: { id } });
  }

  guardar(admin: AdministradorEnt): Observable<AdministradorEnt> {
    const payload = {
      nombre: admin.nombre,
      usuario: admin.usuario,
      contraseña: admin.contrasena
    };
    return this.http.post<AdministradorEnt>(`${this.url}/guardar`, payload);
  }

  eliminar(id: number): Observable<string> {
    return this.http.delete(`${this.url}/eliminar`, { params: { id }, responseType: 'text' });
  }

  login(usuario: string, contraseña: string): Observable<AdministradorEnt> {
    return this.http.post<AdministradorEnt>(`${this.url}/iniciarSesion`, null, {
      params: { usuario, contraseña }
    });
  }
}