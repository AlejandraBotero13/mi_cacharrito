import { CommonModule } from '@angular/common';
import { Component, computed, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UsuarioServ } from '../../Servicio/usuario-serv';

export class Usuario {
  cc: string = '';
  nombre: string = '';
  apellido: string = '';
  fechaNacimiento: string = '';
  telefono: string = '';
}

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.css',
})
export class Usuarios implements OnInit {
  usuarios = signal<Usuario[]>([]);
  usuario: Usuario = new Usuario();

  // Si no hay paginación real, datosPaginados retorna todos
  datosPaginados = computed(() => this.usuarios());

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
    this.usuario = new Usuario();
    const modal = document.getElementById('registro');
    if (modal) modal.style.display = 'block';
  }

  cerrarModal(): void {
    const modal = document.getElementById('registro');
    if (modal) modal.style.display = 'none';
  }

  guardarUsuario(): void {
    this.ServicioUsuario.guardarUsuario(this.usuario).subscribe(() => {
      this.listarU();
      this.cerrarModal();
    });
  }
}