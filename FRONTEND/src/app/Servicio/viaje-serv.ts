import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ViajeEnt,ViajeResumen } from '../Entidad/viaje-ent';

@Injectable({
  providedIn: 'root'
})
export class ViajeServ {

  private url = 'http://localhost:8080/viajes/v';
  private urlAuto = 'http://localhost:8080/automoviles';

  constructor(private http: HttpClient) {}

  listarViajes(): Observable<ViajeEnt[]> {
    return this.http.get<ViajeEnt[]>(`${this.url}/listarViajes`);
  }

  guardarViaje(viaje: ViajeEnt): Observable<ViajeEnt> {
    return this.http.post<ViajeEnt>(`${this.url}/crearViaje`, null, {
      params: {
        fecha: viaje.fecha,
        horaSalida: viaje.horaSalida,
        precio: viaje.precio.toString(),
        lugarSalida: viaje.lugarSalida,
        estado: viaje.estado
      }
    });
  }

  eliminarViaje(id: number): Observable<string> {
    return this.http.delete(`${this.url}/eliminarViaje`, { 
      params: { id }, 
      responseType: 'text' 
    });
  }

  buscarPorId(id: number): Observable<ViajeEnt> {
    return this.http.get<ViajeEnt>(`${this.url}/buscarViaje/${id}`);
  }

  asignarAutomovil(idViaje: number, idAuto: number): Observable<string> {
    return this.http.post(`${this.url}/asignarAutomovil`, null, {
      params: { idViaje, idAuto }, 
      responseType: 'text'
    });
  }
  idYPlaca(): Observable<ViajeResumen[]> {
  return this.http.get<ViajeResumen[]>(`${this.url}/idYPlaca`);
}

actualizarViaje(id: number, viaje: ViajeEnt): Observable<ViajeEnt> {
  return this.http.post<ViajeEnt>(`${this.url}/actualizarViaje`, null, {
    params: {
      id: id.toString(),
      fecha: viaje.fecha,
      horaSalida: viaje.horaSalida,
      precio: viaje.precio.toString(),
      lugarSalida: viaje.lugarSalida,
      estado: viaje.estado
    }
  });
}

}