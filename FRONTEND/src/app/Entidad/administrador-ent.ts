export class AdministradorEnt {
  id?: number;
  nombre: string = '';
  usuario: string = '';
  contrasena: string = '';
}

export class ReservaEnt {
  id?: number;
  numeroAsiento: number = 0;
  fechaReserva?: string;
  estado?: string;
  totalPagar?: number;
  usuario?: { cc: string; nombre: string };
  viaje?: { id: number; precio?: number };
  administrador?: { id: number; nombre: string };
}

export class ViajeEnt {
  id?: number;
  fecha: string = '';
  horaSalida: string = '';
  precio: number = 0;
  lugarSalida: string = '';
  estado?: string;
}