export class ViajeEnt {
  id: number = 0;
  fecha: string = '';
  horaSalida: string = '';
  precio: number = 0;
  estado: string = '';
  lugarSalida: string = '';
  automovil: any = null;
}

export interface ViajeResumen {
  id: number;
  placa: string;
}
