import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ReservaEnt } from '../Entidad/reserva-ent';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ReservaServ {
  private url = `${environment.apiUrl}/reservas/r`;

  constructor(private http: HttpClient) {}

  listar(): Observable<ReservaEnt[]> {
    return this.http.get<ReservaEnt[]>(`${this.url}/listarReservas`);
  }

  consultarPorId(id: number): Observable<ReservaEnt> {
    return this.http.get<ReservaEnt>(`${this.url}/consultarReserva`, { params: { id } });
  }

  crear(numAsiento: number, idViaje: number, ccUsuario: string): Observable<ReservaEnt> {
    return this.http.post<ReservaEnt>(`${this.url}/crearReserva`, null, {
      params: { numAsiento, idViaje, ccUsuario }
    });
  }

  crearConAdmin(numAsiento: number, idViaje: number, ccUsuario: string, idAdmin: number): Observable<ReservaEnt> {
    return this.http.post<ReservaEnt>(`${this.url}/crearReservaConAdmin`, null, {
      params: { numAsiento, idViaje, ccUsuario, idAdmin }
    });
  }

  cancelar(id: number): Observable<string> {
    return this.http.post(`${this.url}/cancelarReserva`, null, { params: { id }, responseType: 'text' });
  }

  confirmar(idReserva: number): Observable<string> {
    return this.http.post(`${this.url}/confirmarReserva`, null, { 
      params: { idReserva }, 
      responseType: 'text' 
    });
  }

  eliminar(id: number): Observable<string> {
    return this.http.delete(`${this.url}/eliminarReserva`, { params: { id }, responseType: 'text' });
  }

  verDisponibilidad(idViaje: number): Observable<number[]> {
    return this.http.get<number[]>(`${this.url}/verDisponibilidad`, { params: { idViaje } });
  }

  viajesDisponibles(fecha?: string, destinoId?: number): Observable<any[]> {
    let params: any = {};
    if (fecha) params['fecha'] = fecha;
    if (destinoId) params['destinoId'] = destinoId;
    return this.http.get<any[]>(`${this.url}/viajesDisponibles`, { params });
  }
}