import { Component, OnInit, signal, computed, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReservaServ } from '../../Servicio/reserva-serv';
import { ReservaEnt } from '../../Entidad/reserva-ent';

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

  ccUsuario = '';
  idViaje: number | null = null;
  numAsiento: number | null = null;

  paginaActual = signal(1);
  itemsPorPagina = 5;

  datosPaginados = computed(() => {
    const inicio = (this.paginaActual() - 1) * this.itemsPorPagina;
    return this.reservas().slice(inicio, inicio + this.itemsPorPagina);
  });

  totalPaginas = computed(() => Math.max(1, Math.ceil(this.reservas().length / this.itemsPorPagina)));

  adminLogueado: { id: number; nombre: string } | null = null;

  constructor(private reservaServ: ReservaServ) {}

  ngOnInit(): void {
    const sesion = localStorage.getItem('adminLogueado');
    if (sesion) {
      this.adminLogueado = JSON.parse(sesion);
    }
    this.listar();
  }

  listar(): void {
    this.reservaServ.listar().subscribe(data => {
      this.reservas.set(data);
      this.paginaActual.set(1);
    });
  }

  abrirModal(): void {
    this.ccUsuario = '';
    this.idViaje = null;
    this.numAsiento = null;
    const modal = document.getElementById('modalReserva');
    if (modal) modal.style.display = 'flex';
  }

  cerrarModal(): void {
    const modal = document.getElementById('modalReserva');
    if (modal) modal.style.display = 'none';
  }

  guardarReserva(): void {
    if (!this.ccUsuario || !this.idViaje || !this.numAsiento) {
      alert('Complete todos los campos');
      return;
    }

    const asiento: number = this.numAsiento;
    const viaje: number = this.idViaje;

    if (this.adminLogueado) {
      this.reservaServ.crearConAdmin(asiento, viaje, this.ccUsuario, this.adminLogueado.id).subscribe(data => {
        this.listar();
        this.cerrarModal();
      });
    } else {
      this.reservaServ.crear(asiento, viaje, this.ccUsuario).subscribe(data => {
        this.listar();
        this.cerrarModal();
      });
    }
  }

  confirmarReserva(id: number): void {
    if (confirm('¿Confirmar y marcar como pagada?')) {
      this.reservaServ.confirmar(id).subscribe(data => {
        this.listar();
      });
    }
  }

  cancelarReserva(id: number): void {
    if (confirm('¿Cancelar esta reserva?')) {
      this.reservaServ.cancelar(id).subscribe(data => {
        this.listar();
      });
    }
  }

  eliminarReserva(id: number): void {
    if (confirm('¿Eliminar esta reserva?')) {
      this.reservaServ.eliminar(id).subscribe(data => {
        this.listar();
      });
    }
  }

  buscarReserva(): void {
    const idInput = (document.getElementById('idReserva') as HTMLInputElement).value;
    const id = parseInt(idInput);
    if (isNaN(id)) return;
    this.reservaServ.consultarPorId(id).subscribe(data => {
      this.reservas.set([data]);
      this.paginaActual.set(1);
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
      default: return 'badge-pendiente';
    }
  }
}