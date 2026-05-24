import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AdministradorEnt, ReservaEnt, ViajeEnt } from '../Entidad/administrador-ent';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AdministradorServ {

  private url = `${environment.apiUrl}/administradores/a`;
  private urlReservas = `${environment.apiUrl}/reservas/r`;

  constructor(private http: HttpClient) {}

  listar(): Observable<AdministradorEnt[]> {
    return this.http.get<AdministradorEnt[]>(`${this.url}/listar`);
  }

  buscarPorId(id: number): Observable<AdministradorEnt> {
    return this.http.get<AdministradorEnt>(`${this.url}/buscarId`, { params: { id } });
  }

  login(usuario: string, contrasena: string): Observable<AdministradorEnt> {
    return this.http.post<AdministradorEnt>(`${this.url}/iniciarSesion`, null, {
      params: { usuario, contrasena }
    });
  }

  listarReservasDelDia(): Observable<ReservaEnt[]> {
    return this.http.get<ReservaEnt[]>(`${this.url}/reservasDelDia`);
  }

  buscarReservaPorId(id: number): Observable<ReservaEnt> {
    return this.http.get<ReservaEnt>(`${this.urlReservas}/consultarReserva`, { params: { id } });
  }

  cancelarReserva(idReserva: number): Observable<string> {
    return this.http.post(`${this.urlReservas}/cancelarReserva`, null, {
      params: { id: idReserva }, responseType: 'text'
    });
  }

  modificarReserva(idReserva: number, numAsiento: number | null, idViaje: number | null, estado: string): Observable<any> {
    let params: any = { id: idReserva };
    if (numAsiento !== null && numAsiento !== 0) 
      params['numAsiento'] = numAsiento;
    if (idViaje !== null && idViaje !== 0)       
      params['idViaje']    = idViaje;
    if (estado)                                  
      params['estado']     = estado;
    return this.http.post<any>(`${this.urlReservas}/actualizarReserva`, null, { params });
  }

  registrarPago(idReserva: number): Observable<string> {
    return this.http.post(`${this.url}/registrarPago`, null, {
      params: { idReserva }, responseType: 'text'
    });
  }

  listarViajes(): Observable<ViajeEnt[]> {
    return this.http.get<ViajeEnt[]>(`${this.url}/listarViajes`);
  }

  actualizarViaje(id: number, fecha: string, horaSalida: string, precio: number, lugarSalida: string, estado?: string): Observable<any> {
    let params: any = { id, fecha, horaSalida, precio, lugarSalida };
    if (estado) params['estado'] = estado;
    return this.http.post<any>(`${this.url}/actualizarViaje`, null, { params });
  }

  pasajerosViaje(idViaje: number): Observable<any> {
    return this.http.get<any>(`${this.url}/pasajerosViaje`, { params: { idViaje } });
  }
}