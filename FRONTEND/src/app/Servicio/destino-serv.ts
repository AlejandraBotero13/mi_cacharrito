import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DestinoEnt } from '../Entidad/destino-ent';

@Injectable({

  providedIn: 'root'
})
export class DestinoServ {

  private apiUrl = 'http://localhost:8080/destino/d';
 
  constructor(private http: HttpClient) {}

  listarDestinos(): Observable<DestinoEnt[]> {
    return this.http.get<DestinoEnt[]>(`${this.apiUrl}/listarDestinos`);
  }

  crearDestino(nombre: string, descripcion: string): Observable<string> {
    const params = new HttpParams()
      .set('nombre', nombre)
      .set('descripcion', descripcion);
    return this.http.post(`${this.apiUrl}/crearDestino`, null, { params, responseType: 'text' });
  }

  eliminarDestino(id: number): Observable<string> {
    const params = new HttpParams().set('id', id);
    return this.http.delete(`${this.apiUrl}/eliminarDestino`, { params, responseType: 'text' });
  }

  actualizarDestino(id: number, nombre: string, descripcion: string): Observable<string> {
    const params = new HttpParams()
      .set('id', id)
      .set('nombre', nombre)
      .set('descripcion', descripcion);
    return this.http.post(`${this.apiUrl}/actualizarDestino`, null, { params, responseType: 'text' });
  }

  obtenerInformacion(id: number): Observable<string> {
    const params = new HttpParams().set('id', id);
    return this.http.get(`${this.apiUrl}/obtenerInformacion`, { params, responseType: 'text' });
  }
}
