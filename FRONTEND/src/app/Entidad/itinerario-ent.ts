export class ItinerarioEnt {
  ordenVisita: number = 0;
  destino: { id: number; nombre: string; descripcion: string } = { id: 0, nombre: '', descripcion: '' };
  viaje: { id: number; fecha: string; lugarSalida: string } = { id: 0, fecha: '', lugarSalida: '' };
}

