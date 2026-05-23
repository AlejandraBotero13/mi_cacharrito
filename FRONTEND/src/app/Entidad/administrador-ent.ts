// ================================================
// ENTIDAD: Administrador
// ================================================
export class AdministradorEnt {
  id?: number;
  nombre: string = '';
  usuario: string = '';
  contrasena: string = '';
}

// ================================================
// ENTIDAD: Reserva
// ================================================
export class ReservaEnt {
  id?: number;
  numAsiento: number = 0;
  idViaje: number = 0;
  ccUsuario: string = '';
  idAdmin: number = 0;
  estado?: string;
  totalPagar?: number;
  fechaReserva?: string;
}

// ================================================
// ENTIDAD: Viaje
// ================================================
export class ViajeEnt {
  id?: number;
  fecha: string = '';
  horaSalida: string = '';
  precio: number = 0;
  lugarSalida: string = '';
  estado?: string;
}