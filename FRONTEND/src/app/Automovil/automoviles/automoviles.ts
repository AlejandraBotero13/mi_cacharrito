import { Component, OnInit, signal, computed, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AutomovilServ } from '../../Servicio/automovil-serv';
import { AutomovilEnt } from '../../Entidad/automovil-ent';

@Component({
  selector: 'app-automoviles',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './automoviles.html',
  styleUrls: ['./automoviles.css'],
  encapsulation: ViewEncapsulation.None
})
export class Automoviles implements OnInit {

  automoviles = signal<AutomovilEnt[]>([]);
  automovil: AutomovilEnt = new AutomovilEnt();
  modalVisible: boolean = false;

  modalViajesVisible: boolean = false;
  viajesDetalle: any[] = [];

  fechaBusqueda: string = '';

  estadosPorAuto: Map<number, string> = new Map();
  conteoViajes: Map<number, number> = new Map();

  paginaActual = signal<number>(1);
  itemsPorPagina = 5;

  totalPaginas = computed(() =>
    Math.max(1, Math.ceil(this.automoviles().length / this.itemsPorPagina))
  );

  datosPaginados = computed(() => {
    const inicio = (this.paginaActual() - 1) * this.itemsPorPagina;
    return this.automoviles().slice(inicio, inicio + this.itemsPorPagina);
  });

  constructor(private automovilService: AutomovilServ) {}

  ngOnInit(): void {
    this.cargarAutomoviles();
  }

  cargarAutomoviles(): void {
    this.automovilService.listarAutomoviles().subscribe((data) => {
      if (data) {
        this.automoviles.set(data);
        this.cargarEstados(data);
        data.forEach(a => {
          this.automovilService.viajesPorAuto(a.id).subscribe((viajes) => {
            if (viajes) this.conteoViajes.set(a.id, viajes.length);
          });
        });
      }
    });
  }

  cargarEstados(autos: AutomovilEnt[]): void {
    this.automovilService.autosEnViaje().subscribe((enViaje) => {
      if (enViaje) {
        const idsEnViaje = new Set(enViaje.map(a => a.id));
        const mapa = new Map<number, string>();
        for (const a of autos) {
          mapa.set(a.id, idsEnViaje.has(a.id) ? 'En viaje' : 'Disponible');
        }
        this.estadosPorAuto = mapa;
      }
    });
  }

  abrirModal(): void {
    this.automovil = new AutomovilEnt();
    this.modalVisible = true;
  }

  cerrarModal(): void {
    this.modalVisible = false;
  }

  guardarAutomovil(): void {
    if (!this.automovil.placa || !this.automovil.capacidad || !this.automovil.modelo || !this.automovil.marca) {
      alert('Todos los campos son obligatorios.');
      return;
    }
    if (this.automovil.id > 0) {
      this.automovilService.actualizarAutomovil(this.automovil.id, this.automovil.placa, this.automovil.capacidad, this.automovil.modelo, this.automovil.marca).subscribe((dato) => {
        if (dato) { this.cargarAutomoviles(); this.cerrarModal(); }
      });
    } else {
      this.automovilService.crearAutomovil(this.automovil.placa, this.automovil.capacidad, this.automovil.modelo, this.automovil.marca).subscribe((dato) => {
        if (dato) { this.cargarAutomoviles(); this.cerrarModal(); }
      });
    }
  }

  eliminarAutomovil(id: number): void {
    if (!confirm('¿Eliminar este automóvil?')) return;
    this.automovilService.eliminarAutomovil(id).subscribe(
      dato => {
        if (dato) this.cargarAutomoviles();
      },
      err => {
        const msg = err.error?.message || err.error || '';
        if (msg.includes('foreign key') || msg.includes('constraint')) {
          alert('No se puede eliminar: el automóvil tiene viajes asociados.');
        } else {
          alert('Error al eliminar el automóvil.');
        }
      }
    );
  }

  editarAutomovil(a: AutomovilEnt): void {
    this.automovil = { ...a };
    this.modalVisible = true;
  }

  verViajes(id: number): void {
    this.automovilService.viajesPorAuto(id).subscribe((data) => {
      if (data) { this.viajesDetalle = data; this.modalViajesVisible = true; }
    });
  }

  cerrarModalViajes(): void {
    this.modalViajesVisible = false;
    this.viajesDetalle = [];
  }

  buscarDisponibles(): void {
    if (!this.fechaBusqueda) { alert('Ingresa una fecha.'); return; }
    this.automovilService.hayMovimientos(this.fechaBusqueda).subscribe((hayMovimientos) => {
      if (!hayMovimientos) {
        alert('No hay movimientos registrados para esa fecha.');
      } else {
        this.automovilService.autosDisponiblesPorFecha(this.fechaBusqueda).subscribe((data) => {
          if (data) {
            this.automoviles.set(data);
            this.paginaActual.set(1);
            data.forEach(a => {
              this.automovilService.viajesPorAuto(a.id).subscribe((viajes) => {
                if (viajes) this.conteoViajes.set(a.id, viajes.length);
              });
            });
          }
        });
      }
    });
  }

  cambiarPagina(pagina: number): void {
    if (pagina >= 1 && pagina <= this.totalPaginas()) {
      this.paginaActual.set(pagina);
    }
  }
}

