import { Component, OnInit, signal, computed, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DestinoServ } from '../../Servicio/destino-serv';
import { DestinoEnt } from '../../Entidad/destino-ent';

@Component({
  selector: 'app-destinos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './destinos.html',
  styleUrls: ['./destinos.css'],
  encapsulation: ViewEncapsulation.None
})
export class Destinos implements OnInit {

  destinos = signal<DestinoEnt[]>([]);
  destino: DestinoEnt = new DestinoEnt();
  modalVisible: boolean = false;

  paginaActual = signal<number>(1);
  itemsPorPagina = 5;

  totalPaginas = computed(() =>
    Math.max(1, Math.ceil(this.destinos().length / this.itemsPorPagina))
  );

  datosPaginados = computed(() => {
    const inicio = (this.paginaActual() - 1) * this.itemsPorPagina;
    return this.destinos().slice(inicio, inicio + this.itemsPorPagina);
  });

  constructor(private destinoService: DestinoServ) {}

  ngOnInit(): void {
    this.cargarDestinos();
  }

  cargarDestinos(): void {
    this.destinoService.listarDestinos().subscribe(
      (data) => { this.destinos.set(data); },
      () => {}
    );
  }

  abrirModal(): void {
    this.destino = new DestinoEnt();
    this.modalVisible = true;
  }

  cerrarModal(): void {
    this.modalVisible = false;
  }

  guardarDestino(): void {
    if (!this.destino.nombre || !this.destino.descripcion) {
      alert('Nombre y descripción son obligatorios.');
      return;
    }

    if (this.destino.id > 0) {
      this.destinoService.actualizarDestino(this.destino.id, this.destino.nombre, this.destino.descripcion).subscribe(
        () => { this.cargarDestinos(); this.cerrarModal(); },
        () => {}
      );
    } else {
      this.destinoService.crearDestino(this.destino.nombre, this.destino.descripcion).subscribe(
        () => { this.cargarDestinos(); this.cerrarModal(); },
        () => {}
      );
    }
  }

  eliminarDestino(id: number): void {
    if (!confirm('¿Eliminar este destino?')) return;
    this.destinoService.eliminarDestino(id).subscribe(
      () => { this.cargarDestinos(); },
      () => {}
    );
  }

  editarDestino(d: DestinoEnt): void {
    this.destino = { ...d };
    this.modalVisible = true;
  }

  cambiarPagina(pagina: number): void {
    if (pagina >= 1 && pagina <= this.totalPaginas()) {
      this.paginaActual.set(pagina);
    }
  }

  // Propiedades
busquedaId: string = '';

// Método
buscarPorId(): void {
  const id = Number(this.busquedaId);
  if (!this.busquedaId || isNaN(id)) {
    this.cargarDestinos();
    return;
  }
  this.destinoService.listarDestinos().subscribe(
    (data) => {
      const resultado = data.filter(d => d.id === id);
      this.destinos.set(resultado);
      this.paginaActual.set(1);
    },
    () => {}
  );
}
}
