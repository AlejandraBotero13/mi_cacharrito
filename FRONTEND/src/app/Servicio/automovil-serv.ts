import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AutomovilEnt } from '../Entidad/automovil-ent';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AutomovilServ {

  private apiUrl = `${environment.apiUrl}/automoviles/au`;

  constructor(private http: HttpClient) {}

  listarAutomoviles(): Observable<AutomovilEnt[]> {
    return this.http.get<AutomovilEnt[]>(`${this.apiUrl}/listar`);
  }

  crearAutomovil(placa: string, capacidad: number, modelo: number, marca: string): Observable<any> {
    const params = new HttpParams()
      .set('placa', placa)
      .set('capacidad', capacidad)
      .set('modelo', modelo)
      .set('marca', marca);
    return this.http.post(`${this.apiUrl}/crear`, null, { params });
  }

  eliminarAutomovil(id: number): Observable<string> {
    const params = new HttpParams().set('id', id);
    return this.http.delete(`${this.apiUrl}/eliminar`, { params, responseType: 'text' });
  }

  actualizarAutomovil(id: number, placa: string, capacidad: number, modelo: number, marca: string): Observable<any> {
    const params = new HttpParams()
      .set('id', id)
      .set('placa', placa)
      .set('capacidad', capacidad)
      .set('modelo', modelo)
      .set('marca', marca);
    return this.http.post(`${this.apiUrl}/actualizarAutomovil`, null, { params });
  }

  viajesPorAuto(id: number): Observable<any[]> {
    const params = new HttpParams().set('id', id);
    return this.http.get<any[]>(`${this.apiUrl}/viajesPorAuto`, { params });
  }

  autosDisponiblesPorFecha(fecha: string): Observable<AutomovilEnt[]> {
    const params = new HttpParams().set('fecha', fecha);
    return this.http.get<AutomovilEnt[]>(`${this.apiUrl}/disponiblesPorFecha`, { params });
  }


  autosEnViaje(): Observable<AutomovilEnt[]> {
    return this.http.get<AutomovilEnt[]>(`${this.apiUrl}/enViaje`);
  }

  estadoPorFecha(fecha: string): Observable<any[]> {
  return this.http.get<any[]>(`${this.apiUrl}/estadoPorFecha`, {
    params: new HttpParams().set('fecha', fecha)
  });
}

hayMovimientos(fecha: string): Observable<boolean> {
  return this.http.get<boolean>(`${this.apiUrl}/hayMovimientos`, {
    params: new HttpParams().set('fecha', fecha)
  });
}
}