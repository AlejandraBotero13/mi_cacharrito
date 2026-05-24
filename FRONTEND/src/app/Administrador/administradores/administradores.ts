import { Component, OnInit, signal, computed, ViewEncapsulation, ChangeDetectorRef } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AdministradorServ } from '../../Servicio/administrador-serv';
import { AdministradorEnt, ReservaEnt } from '../../Entidad/administrador-ent';

@Component({
  selector: 'app-administradores',
  standalone: true,
  imports: [CommonModule, FormsModule, CurrencyPipe],
  templateUrl: './administradores.html',
  styleUrls: ['./administradores.css'],
  encapsulation: ViewEncapsulation.None
})
export class Administradores implements OnInit {

  admins = signal<AdministradorEnt[]>([]);
  paginaActual = signal(1);
  itemsPorPagina = 5;

  datosPaginados = computed(() => {
    const inicio = (this.paginaActual() - 1) * this.itemsPorPagina;
    const fin = inicio + this.itemsPorPagina;
    return this.admins().slice(inicio, fin);
  });

  totalPaginas = computed(() =>
    Math.max(1, Math.ceil(this.admins().length / this.itemsPorPagina))
  );

  cancelarReservaId: number = 0;
  modificarReservaData: any = { idReserva: 0, numAsiento: null, idViaje: null, estado: '' };
  pagoReservaId: number = 0;
  viajeIdPasajeros: number = 0;
  pasajerosResultado: any = null;
  reservasDelDia: ReservaEnt[] = [];
  cargandoReserva: boolean = false;

  constructor(private adminServ: AdministradorServ, private router: Router, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    const sesion = localStorage.getItem('adminLogueado');
    if (!sesion) {
      this.router.navigate(['/login-admin']);
    } else {
      this.listarAdmins();
    }
  }

  private abrirModalId(id: string): void {
    const modal = document.getElementById(id);
    if (modal) modal.style.display = 'flex';
  }

  private cerrarModalId(id: string): void {
    const modal = document.getElementById(id);
    if (modal) modal.style.display = 'none';
  }

  listarAdmins(): void {
    this.adminServ.listar().subscribe(
      data => { this.admins.set(data); this.paginaActual.set(1); },
      () => alert('Error al cargar administradores')
    );
  }

  buscarAdmin(): void {
    const idInput = (document.getElementById('idAdmin') as HTMLInputElement).value;
    const id = parseInt(idInput);
    if (isNaN(id)) { alert('Ingrese un ID válido'); return; }
    this.adminServ.buscarPorId(id).subscribe(
      admin => { this.admins.set([admin]); this.paginaActual.set(1); },
      () => alert('No se encontró administrador con ese ID')
    );
  }

  cambiarPagina(nueva: number): void {
    if (nueva >= 1 && nueva <= this.totalPaginas()) {
      this.paginaActual.set(nueva);
    }
  }

  cerrarSesion(): void {
    localStorage.removeItem('adminLogueado');
    this.router.navigate(['/login-admin']);
  }

  abrirModalReservasDelDia(): void {
    this.reservasDelDia = [];
    this.adminServ.listarReservasDelDia().subscribe(
      data => { this.reservasDelDia = data; this.cdr.detectChanges(); this.abrirModalId('modalReservasDelDia'); },
      () => alert('Error al obtener reservas del día')
    );
  }

  cerrarModalReservasDelDia(): void { this.cerrarModalId('modalReservasDelDia'); }

  abrirModalCancelarReserva(): void {
    this.cancelarReservaId = 0;
    this.abrirModalId('modalCancelarReserva');
  }

  cerrarModalCancelarReserva(): void { this.cerrarModalId('modalCancelarReserva'); }

  cancelarReserva(): void {
    if (!this.cancelarReservaId) { alert('Ingrese el ID de la reserva'); return; }
    if (confirm(`¿Cancelar reserva #${this.cancelarReservaId}?`)) {
      this.adminServ.cancelarReserva(this.cancelarReservaId).subscribe(
        msg => { alert(msg); this.cerrarModalCancelarReserva(); },
        err => alert('Error: ' + (err.error || 'No se pudo cancelar'))
      );
    }
  }

  abrirModalModificarReserva(): void {
    this.modificarReservaData = { idReserva: null, numAsiento: null, idViaje: null, estado: '' };
    this.cargandoReserva = false;
    this.abrirModalId('modalModificarReserva');
  }

  cerrarModalModificarReserva(): void { this.cerrarModalId('modalModificarReserva'); }

  buscarReservaParaModificar(): void {
    const id = this.modificarReservaData.idReserva;
    if (!id) return;
    this.cargandoReserva = true;
    this.adminServ.buscarReservaPorId(id).subscribe(
      (r: any) => {
        this.modificarReservaData = {
          idReserva: r.id,
          numAsiento: r.numeroAsiento ?? r.numAsiento,
          idViaje: r.viaje?.id ?? r.idViaje,
          estado: r.estado || ''
        };
        this.cargandoReserva = false;
        this.cdr.detectChanges();
      },
      (err) => {
        alert(err.status === 404 ? 'No existe una reserva con ese ID' : 'Error: ' + err.message);
        this.cargandoReserva = false;
        this.cdr.detectChanges();
      }
    );
  }

  modificarReserva(): void {
    if (!this.modificarReservaData.idReserva) { alert('Ingrese el ID de la reserva'); return; }
    const { idReserva, numAsiento, idViaje, estado } = this.modificarReservaData;
    this.adminServ.modificarReserva(idReserva, numAsiento, idViaje, estado).subscribe(
      () => { alert('Reserva modificada correctamente'); this.cerrarModalModificarReserva(); },
      err => alert('Error: ' + (err.error || 'No se pudo modificar'))
    );
  }

  abrirModalRegistrarPago(): void {
    this.pagoReservaId = 0;
    this.abrirModalId('modalRegistrarPago');
  }

  cerrarModalRegistrarPago(): void { this.cerrarModalId('modalRegistrarPago'); }

  registrarPago(): void {
    if (!this.pagoReservaId) { alert('Ingrese el ID de la reserva'); return; }
    this.adminServ.registrarPago(this.pagoReservaId).subscribe(
      msg => { alert(msg); this.cerrarModalRegistrarPago(); },
      err => alert('Error: ' + (err.error || 'No se pudo registrar el pago'))
    );
  }

  abrirModalPasajerosViaje(): void {
    this.viajeIdPasajeros = 0;
    this.pasajerosResultado = null;
    this.abrirModalId('modalPasajerosViaje');
  }

  cerrarModalPasajerosViaje(): void { this.cerrarModalId('modalPasajerosViaje'); }

  verPasajerosViaje(): void {
    if (!this.viajeIdPasajeros) { alert('Ingrese el ID del viaje'); return; }
    this.adminServ.pasajerosViaje(this.viajeIdPasajeros).subscribe(
      data => { this.pasajerosResultado = data; this.cdr.detectChanges(); },
      err => alert('Error: ' + (err.error || 'No se encontró el viaje'))
    );
  }
}