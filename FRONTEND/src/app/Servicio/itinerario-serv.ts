import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ItinerarioEnt } from '../Entidad/itinerario-ent';

@Injectable({ providedIn: 'root' })
export class ItinerarioServ {

  private url = 'http://localhost:8080/itinerarios/i';

  constructor(private http: HttpClient) {}

  listarPorViaje(idViaje: number): Observable<ItinerarioEnt[]> {
    return this.http.get<ItinerarioEnt[]>(`${this.url}/listarDestinos`, {
      params: new HttpParams().set('idViaje', idViaje)
    });
  }

  listarTodos(): Observable<ItinerarioEnt[]> {
    return this.http.get<ItinerarioEnt[]>(`${this.url}/listarOrdenado`);
  }

  crearItinerario(idViaje: number, idDestino: number, orden: number): Observable<any> {
    return this.http.post(`${this.url}/crearItinerario`, null, {
      params: new HttpParams()
        .set('idViaje', idViaje)
        .set('idDestino', idDestino)
        .set('orden', orden)
    });
  }

  eliminarDestino(idViaje: number, orden: number): Observable<string> {
    return this.http.delete(`${this.url}/eliminarDestino`, {
      params: new HttpParams().set('idViaje', idViaje).set('orden', orden),
      responseType: 'text'
    });
  }

  actualizarOrden(idViaje: number, ordenActual: number, nuevoOrden: number): Observable<string> {
    return this.http.post(`${this.url}/actualizarItinerario`, null, {
      params: new HttpParams()
        .set('idViaje', idViaje)
        .set('ordenActual', ordenActual)
        .set('nuevoOrden', nuevoOrden),
      responseType: 'text'
    });
  }

  eliminarItinerario(idViaje: number): Observable<string> {
    return this.http.delete(`${this.url}/eliminarItinerario`, {
      params: new HttpParams().set('idViaje', idViaje),
      responseType: 'text'
    });
  }
}