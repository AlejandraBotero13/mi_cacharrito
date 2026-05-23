import { CommonModule } from '@angular/common';
import { Component, computed, OnInit, signal, ViewEncapsulation } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UsuarioServ } from '../../Servicio/usuario-serv';
import { UsuarioEnt } from '../../Entidad/usuario-ent';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.css',
  encapsulation: ViewEncapsulation.None
})
export class Usuarios implements OnInit {

  usuarios = signal<UsuarioEnt[]>([]);
  usuario: UsuarioEnt = new UsuarioEnt();

  paginaActual = signal(1);
  itemsPorPagina = 6;

  datosPaginados = computed(() => {
    const inicio = (this.paginaActual() - 1) * this.itemsPorPagina;
    const fin = inicio + this.itemsPorPagina;
    return this.usuarios().slice(inicio, fin);
  });

  totalPaginas = computed(() =>
    Math.ceil(this.usuarios().length / this.itemsPorPagina)
  );

  cambiarPagina(nuevaPagina: number) {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas()) {
      this.paginaActual.set(nuevaPagina);
    }
  }

  constructor(private ServicioUsuario: UsuarioServ) {}

  ngOnInit(): void {
    this.listarU();
  }

  private listarU(): void {
    this.ServicioUsuario.listarUsuarios().subscribe(dato => {
      this.usuarios.set(dato);
    });
  }

  abrirModal(): void {
    this.usuario = new UsuarioEnt();
    const modal = document.getElementById('registro');
    if (modal) modal.style.display = 'flex';
  }

  cerrarModal(): void {
    const modal = document.getElementById('registro');
    if (modal) modal.style.display = 'none';
  }

  guardarUsuario(): void {
    if (!this.usuario.cc || !this.usuario.nombre || !this.usuario.apellido ||
        !this.usuario.fechaNacimiento || !this.usuario.telefono) {
      alert('Complete todos los campos');
      return;
    }
    this.ServicioUsuario.guardarUsuario(this.usuario).subscribe(dato => {
    if(dato !== null){  
      console.log(dato)
      this.listarU();
      this.cerrarModal();
      this.usuario = new UsuarioEnt();
      alert("Usuario guardado")
    }else{
      alert("No se registro el usuario")
    }
    })
  }

  eliminarUsuario(cc: string): void {
    this.ServicioUsuario.eliminarUsuario(cc).subscribe(dato => {
      if (dato.includes('No se puede eliminar')) {
        alert(dato);
      } else {
        this.listarU();
      }
    });
  }

  buscarUsuario(): void {
    const cc = (document.getElementById('cedula') as HTMLInputElement).value;
    if (!cc) return;
    this.ServicioUsuario.buscarPorCc(cc).subscribe((dato: UsuarioEnt) => {
      this.usuarios.set([dato]);
      this.paginaActual.set(1);
    });
  }
}

