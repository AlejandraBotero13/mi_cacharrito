import { Component, OnInit, signal, computed, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AdministradorServ } from '../../Servicio/administrador-serv';
import { AdministradorEnt } from '../../Entidad/administrador-ent';

@Component({
  selector: 'app-administradores',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './administradores.html',
  styleUrls: ['./administradores.css'],
  encapsulation: ViewEncapsulation.None
})
export class Administradores implements OnInit {
  admins = signal<AdministradorEnt[]>([]);
  admin: AdministradorEnt = new AdministradorEnt();

  paginaActual = signal(1);
  itemsPorPagina = 5;

  datosPaginados = computed(() => {
    const inicio = (this.paginaActual() - 1) * this.itemsPorPagina;
    const fin = inicio + this.itemsPorPagina;
    return this.admins().slice(inicio, fin);
  });

  totalPaginas = computed(() => Math.ceil(this.admins().length / this.itemsPorPagina));

  constructor(private adminServ: AdministradorServ, private router: Router) {}

  ngOnInit(): void {
    // Verificar si hay sesión activa
    const sesion = localStorage.getItem('adminLogueado');
    if (!sesion) {
      this.router.navigate(['/login-admin']);
    } else {
      this.listarAdmins();
    }
  }

  listarAdmins(): void {
    this.adminServ.listar().subscribe(data => {
      this.admins.set(data);
    });
  }

  abrirModal(): void {
    this.admin = new AdministradorEnt();
    const modal = document.getElementById('registro');
    if (modal) modal.style.display = 'flex';
  }

  cerrarModal(): void {
    const modal = document.getElementById('registro');
    if (modal) modal.style.display = 'none';
  }

  guardarAdmin(): void {
    if (!this.admin.nombre || !this.admin.usuario || !this.admin.contrasena) {
      alert('Complete todos los campos');
      return;
    }
    this.adminServ.guardar(this.admin).subscribe(() => {
      this.listarAdmins();
      this.cerrarModal();
    });
  }

  eliminarAdmin(id: number): void {
    if (confirm('¿Eliminar administrador?')) {
      this.adminServ.eliminar(id).subscribe(() => {
        this.listarAdmins();
      });
    }
  }

  buscarAdmin(): void {
    const idInput = (document.getElementById('idAdmin') as HTMLInputElement).value;
    const id = parseInt(idInput);
    if (isNaN(id)) return;
    this.adminServ.buscarPorId(id).subscribe(admin => {
      this.admins.set([admin]);
      this.paginaActual.set(1);
    }, () => {
      alert('No se encontró administrador con ese ID');
    });
  }

  cambiarPagina(nueva: number) {
    if (nueva >= 1 && nueva <= this.totalPaginas()) {
      this.paginaActual.set(nueva);
    }
  }

  cerrarSesion() {
    localStorage.removeItem('adminLogueado');
    window.location.href = '/login-admin';
  }
}