import { Component, OnInit, signal, computed, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReservaServ } from '../../Servicio/reserva-serv';
import { ReservaEnt } from '../../Entidad/reserva-ent';
import { UsuarioServ } from '../../Servicio/usuario-serv';
import { UsuarioEnt } from '../../Entidad/usuario-ent';

@Component({
  selector: 'app-reservas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reservas.html',
  styleUrls: ['./reservas.css'],
  encapsulation: ViewEncapsulation.None
})
export class Reservas implements OnInit {
  reservas = signal<ReservaEnt[]>([]);
  viajesDisponibles = signal<any[]>([]);
  asientosDisponibles = signal<number[]>([]);

  ccUsuario = '';
  idViaje: number | null = null;
  numAsiento: number | null = null;
  filtroFecha: string = '';

  usuarioEncontrado: UsuarioEnt | null = null;
  usuarioNoEncontrado = false;

  paginaActual = signal(1);
  itemsPorPagina = 5;

  datosPaginados = computed(() => {
    const inicio = (this.paginaActual() - 1) * this.itemsPorPagina;
    return this.reservas().slice(inicio, inicio + this.itemsPorPagina);
  });

  totalPaginas = computed(() =>
    Math.max(1, Math.ceil(this.reservas().length / this.itemsPorPagina))
  );

  adminLogueado: { id: number; nombre: string } | null = null;

  constructor(
    private reservaServ: ReservaServ,
    private usuarioServ: UsuarioServ
  ) {}

  ngOnInit(): void {
    const sesionAdmin = localStorage.getItem('adminLogueado');
    if (sesionAdmin) this.adminLogueado = JSON.parse(sesionAdmin);
    this.listar();
  }

  listar(): void {
    const pagina = this.paginaActual();
    this.reservaServ.listar().subscribe({
      next: data => { this.reservas.set(data); this.paginaActual.set(pagina); },
      error: err => alert('Error al listar: ' + (err.error || err.message))
    });
  }

  abrirModal(): void {
    this.ccUsuario = '';
    this.idViaje = null;
    this.numAsiento = null;
    this.filtroFecha = '';
    this.usuarioEncontrado = null;
    this.usuarioNoEncontrado = false;
    this.asientosDisponibles.set([]);
    this.cargarViajes();
    const modal = document.getElementById('modalReserva');
    if (modal) modal.style.display = 'flex';
  }

  cerrarModal(): void {
    const modal = document.getElementById('modalReserva');
    if (modal) modal.style.display = 'none';
  }

  cargarViajes(): void {
    this.reservaServ.viajesDisponibles(this.filtroFecha || undefined).subscribe({
      next: data => this.viajesDisponibles.set(data),
      error: err => alert('Error al cargar viajes: ' + (err.error || err.message))
    });
  }

  seleccionarViaje(v: any): void {
    this.idViaje = v.idViaje;
    this.numAsiento = null;
    this.reservaServ.verDisponibilidad(v.idViaje).subscribe({
      next: asientos => this.asientosDisponibles.set(asientos),
      error: err => alert('Error al cargar asientos: ' + (err.error || err.message))
    });
  }

  nombresDestinos(destinos: any[]): string {
    if (!destinos || destinos.length === 0) return 'Sin destinos';
    return destinos.map(d => d.nombre).join(' → ');
  }

  buscarUsuarioPorCc(): void {
    if (!this.ccUsuario) return;
    this.usuarioEncontrado = null;
    this.usuarioNoEncontrado = false;
    this.usuarioServ.buscarPorCc(this.ccUsuario).subscribe({
      next: u => { this.usuarioEncontrado = u; },
      error: () => { this.usuarioNoEncontrado = true; }
    });
  }

  guardarReserva(): void {
    if (!this.idViaje || !this.numAsiento) {
      alert('Seleccione un viaje y un asiento');
      return;
    }
    if (!this.ccUsuario) {
      alert('Ingrese la CC del usuario');
      return;
    }

    const asiento: number = this.numAsiento;
    const viaje: number = this.idViaje;

    if (this.adminLogueado) {
      this.reservaServ.crearConAdmin(asiento, viaje, this.ccUsuario, this.adminLogueado.id).subscribe({
        next: () => { this.listar(); this.cerrarModal(); },
        error: err => alert('Error: ' + (err.error || err.message))
      });
    } else {
      this.reservaServ.crear(asiento, viaje, this.ccUsuario).subscribe({
        next: () => { this.listar(); this.cerrarModal(); },
        error: err => alert('Error: ' + (err.error || err.message))
      });
    }
  }

  confirmarReserva(id: number): void {
    if (confirm('¿Confirmar y marcar como pagada?')) {
      this.reservaServ.confirmar(id).subscribe({
        next: () => { alert('Reserva confirmada'); this.listar(); },
        error: err => alert('Error: ' + (err.error || err.message))
      });
    }
  }

  cancelarReserva(id: number): void {
    if (confirm('¿Cancelar esta reserva?')) {
      this.reservaServ.cancelar(id).subscribe({
        next: () => this.listar(),
        error: err => alert('Error: ' + (err.error || err.message))
      });
    }
  }

  eliminarReserva(id: number): void {
    if (confirm('¿Eliminar esta reserva?')) {
      this.reservaServ.eliminar(id).subscribe({
        next: () => this.listar(),
        error: err => alert('Error: ' + (err.error || err.message))
      });
    }
  }

  buscarReserva(): void {
    const idInput = (document.getElementById('idReserva') as HTMLInputElement).value;
    const id = parseInt(idInput);
    if (isNaN(id)) return;
    this.reservaServ.consultarPorId(id).subscribe({
      next: data => { this.reservas.set([data]); this.paginaActual.set(1); },
      error: err => alert('Error: ' + (err.error || err.message))
    });
  }

  cambiarPagina(nueva: number): void {
    if (nueva >= 1 && nueva <= this.totalPaginas()) {
      this.paginaActual.set(nueva);
    }
  }

  estadoClase(estado?: string): string {
    switch (estado) {
      case 'pagada': return 'badge-pagada';
      case 'cancelada': return 'badge-cancelada';
      case 'finalizada': return 'badge-finalizada';
      default: return 'badge-pendiente';
    }
  }

  buscarPorCc(): void {
    const cc = (document.getElementById('ccBuscar') as HTMLInputElement).value;
    if (!cc) return;
    this.reservaServ.listar().subscribe(
      data => {
        const filtradas = data.filter(r => r.usuario?.cc?.includes(cc));
        this.reservas.set(filtradas);
        this.paginaActual.set(1);
      },
      err => alert('Error: ' + (err.error || err.message))
    );
  }
}