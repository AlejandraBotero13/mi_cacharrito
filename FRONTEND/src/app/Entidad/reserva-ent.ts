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