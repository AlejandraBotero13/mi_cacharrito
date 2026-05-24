import { CommonModule } from '@angular/common';
import { Component, computed, OnInit, signal, ViewEncapsulation } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ViajeServ } from '../../Servicio/viaje-serv';
import { ViajeEnt, ViajeResumen} from '../../Entidad/viaje-ent';

@Component({
  selector: 'app-viajes',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './viajes.html',
  styleUrl: './viajes.css',
  encapsulation: ViewEncapsulation.None
})
export class Viajes implements OnInit {

  viajes = signal<ViajeEnt[]>([]);
  viaje: ViajeEnt = new ViajeEnt();
  idAutoSeleccionado: number | null = null;
  viajesResumen = signal<ViajeResumen[]>([]);
  esEdicion = signal(false);
  paginaActual = signal(1);
  itemsPorPagina = 6;

  datosPaginados = computed(() => {
    const inicio = (this.paginaActual() - 1) * this.itemsPorPagina;
    const fin = inicio + this.itemsPorPagina;
    return this.viajes().slice(inicio, fin);
  });

  totalPaginas = computed(() =>
    Math.ceil(this.viajes().length / this.itemsPorPagina)
  );

  constructor(private ServicioViaje: ViajeServ) {}

  ngOnInit(): void {
    this.listarViajes();
    this.cargarResumen();
  }

  listarViajes(): void {
    this.ServicioViaje.listarViajes().subscribe(dato => {
      if (dato) {
        this.viajes.set(dato);
      }
    });
  }

  abrirModalEdicion(v: ViajeEnt): void {
    this.viaje = { ...v };
    this.idAutoSeleccionado = v.automovil?.id ?? null;
    this.esEdicion.set(true);
    const modal = document.getElementById('registro');
    if (modal) modal.style.display = 'flex';
  }

  abrirModal(): void {
    this.viaje = new ViajeEnt();
    this.idAutoSeleccionado = null;
    this.esEdicion.set(false);
    const modal = document.getElementById('registro');
    if (modal) modal.style.display = 'flex';
  }

  cerrarModal(): void {
    const modal = document.getElementById('registro');
    if (modal) modal.style.display = 'none';
    this.viaje = new ViajeEnt();
    this.idAutoSeleccionado = null;
    this.esEdicion.set(false);
  }

  actualizarViaje(): void {
    if (!this.viaje.fecha || !this.viaje.horaSalida || !this.viaje.precio ||
        !this.viaje.estado || !this.viaje.lugarSalida) {
      alert('Complete todos los campos');
      return;
    }

    this.viaje.lugarSalida = this.viaje.lugarSalida.trim();

    const viajeAEnviar = { ...this.viaje };
    delete viajeAEnviar.automovil;

    this.ServicioViaje.actualizarViaje(this.viaje.id, viajeAEnviar).subscribe(
      dato => {
        if (dato) {
          if (this.idAutoSeleccionado && this.idAutoSeleccionado > 0) {
            this.ServicioViaje.asignarAutomovil(this.viaje.id, this.idAutoSeleccionado).subscribe();
          }
          this.listarViajes();
          this.cerrarModal();
          alert('Viaje actualizado');
        } else {
          alert('No se pudo actualizar');
        }
      },
      err => alert('Error del servidor: ' + (err.error?.message || err.message))
    );
  }

  guardarViaje(): void {
    if (!this.viaje.fecha || !this.viaje.horaSalida || !this.viaje.precio ||
        !this.viaje.estado || !this.viaje.lugarSalida) {
      alert('Complete todos los campos');
      return;
    }

    this.viaje.lugarSalida = this.viaje.lugarSalida.trim();

    const viajeAEnviar = { ...this.viaje };
    delete viajeAEnviar.automovil;

    this.ServicioViaje.guardarViaje(viajeAEnviar).subscribe(
      viajeCreado => {
        if (viajeCreado && viajeCreado.id) {
          if (this.idAutoSeleccionado && this.idAutoSeleccionado > 0) {
            this.ServicioViaje.asignarAutomovil(viajeCreado.id, this.idAutoSeleccionado).subscribe();
          }
          this.listarViajes();
          this.cerrarModal();
          alert('Viaje guardado');
        } else {
          alert('No se registró el viaje');
        }
      },
      err => alert('Error del servidor: ' + (err.error?.message || err.message))
    );
  }

  eliminarViaje(id: number): void {
    if (confirm('¿Está seguro de eliminar este viaje?')) {
      this.ServicioViaje.eliminarViaje(id).subscribe(
        dato => {
          alert(dato);
          this.listarViajes();
        },
        err => alert(err.error || 'No se puede eliminar el viaje')
      );
    }
  }

  buscarViaje(): void {
    const idInput = (document.getElementById('id') as HTMLInputElement).value;

    if (!idInput) {
      this.listarViajes();
      this.paginaActual.set(1);
      return;
    }

    const id = Number(idInput);
    this.ServicioViaje.buscarPorId(id).subscribe(
      viaje => {
        if (viaje && viaje.id) {
          this.viajes.set([viaje]);
          this.paginaActual.set(1);
        } else {
          alert('No se encontró el viaje');
          this.listarViajes();
        }
      },
      err => alert('Error: ' + (err.error || err.message))
    );
  }

  cambiarPagina(nuevaPagina: number) {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas()) {
      this.paginaActual.set(nuevaPagina);
    }
  }

  cargarResumen(): void {
    this.ServicioViaje.listarAutomoviles().subscribe(dato => {
      this.viajesResumen.set(dato.map((a: any) => ({ id: a.id, placa: a.placa })));
    });
  }
}