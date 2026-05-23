import { Component, OnInit, signal, computed, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ItinerarioServ } from '../../Servicio/itinerario-serv';
import { ItinerarioEnt } from '../../Entidad/itinerario-ent';

@Component({
  selector: 'app-itinerarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './itinerarios.html',
  styleUrls: ['./itinerarios.css'],
  encapsulation: ViewEncapsulation.None
})
export class Itinerarios implements OnInit {

  itinerarios = signal<ItinerarioEnt[]>([]);
  modalVisible = false;
  modalActualizar = false;

  idViaje: number = 0;
  idDestino: number = 0;
  orden: number = 0;
  ordenActual: number = 0;
  nuevoOrden: number = 0;
  busquedaViaje: string = '';

  paginaActual = signal(1);
  itemsPorPagina = 5;

  totalPaginas = computed(() =>
    Math.max(1, Math.ceil(this.itinerarios().length / this.itemsPorPagina))
  );

  datosPaginados = computed(() => {
    const inicio = (this.paginaActual() - 1) * this.itemsPorPagina;
    return this.itinerarios().slice(inicio, inicio + this.itemsPorPagina);
  });

  constructor(private itinerarioServ: ItinerarioServ) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.itinerarioServ.listarTodos().subscribe({ next: data => this.itinerarios.set(data) });
  }

  buscarPorViaje(): void {
    const id = Number(this.busquedaViaje);
    if (!this.busquedaViaje || isNaN(id)) { this.cargar(); return; }
    this.itinerarioServ.listarPorViaje(id).subscribe({
      next: data => { this.itinerarios.set(data); this.paginaActual.set(1); }
    });
  }

  abrirModal(): void { this.idViaje = 0; this.idDestino = 0; this.orden = 0; this.modalVisible = true; }
  cerrarModal(): void { this.modalVisible = false; }

  guardar(): void {
    if (!this.idViaje || !this.idDestino || !this.orden) { alert('Complete todos los campos'); return; }
    this.itinerarioServ.crearItinerario(this.idViaje, this.idDestino, this.orden).subscribe({
      next: () => { this.cargar(); this.cerrarModal(); }
    });
  }

  eliminarDestino(idViaje: number, orden: number): void {
    if (!confirm('¿Eliminar este destino del itinerario?')) return;
    this.itinerarioServ.eliminarDestino(idViaje, orden).subscribe({
      next: () => this.cargar()
    });
  }

  abrirActualizar(it: ItinerarioEnt): void {
    this.idViaje = it.viaje.id;
    this.ordenActual = it.ordenVisita;
    this.nuevoOrden = it.ordenVisita;
    this.modalActualizar = true;
  }

  cerrarActualizar(): void { this.modalActualizar = false; }

  actualizarOrden(): void {
    this.itinerarioServ.actualizarOrden(this.idViaje, this.ordenActual, this.nuevoOrden).subscribe({
      next: () => { this.cargar(); this.cerrarActualizar(); }
    });
  }

  eliminarItinerario(idViaje: number): void {
    if (!confirm('¿Eliminar todo el itinerario de este viaje?')) return;
    this.itinerarioServ.eliminarItinerario(idViaje).subscribe({
      next: () => this.cargar()
    });
  }

  cambiarPagina(p: number): void {
    if (p >= 1 && p <= this.totalPaginas()) this.paginaActual.set(p);
  }
}