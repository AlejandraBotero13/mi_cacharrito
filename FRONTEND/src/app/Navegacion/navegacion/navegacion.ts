import { CommonModule } from '@angular/common';
import { Component, OnInit, HostListener } from '@angular/core';
import { Router, NavigationEnd, RouterLink } from '@angular/router';

@Component({
  selector: 'app-navegacion',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './navegacion.html',
  styleUrl: './navegacion.css',
})
export class Navegacion implements OnInit {
  menuAbierto: boolean = false;
  perfilAbierto: boolean = false;
  isLoggedIn = false;
  adminNombre = '';
  adminIniciales = '';

  constructor(private router: Router) {}

  ngOnInit() {
    this.actualizarEstadoLogin();
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.actualizarEstadoLogin();
      }
    });
  }

  actualizarEstadoLogin() {
    const raw = localStorage.getItem('adminLogueado');
    if (raw) {
      const admin = JSON.parse(raw);
      this.isLoggedIn = true;
      this.adminNombre = admin.nombre || admin.usuario || 'Admin';
      // Iniciales: primeras letras de cada palabra (máx 2)
      this.adminIniciales = this.adminNombre
        .split(' ')
        .slice(0, 2)
        .map((p: string) => p[0]?.toUpperCase() || '')
        .join('');
    } else {
      this.isLoggedIn = false;
      this.adminNombre = '';
      this.adminIniciales = '';
    }
  }

  togglePerfil(event: Event) {
    event.stopPropagation();
    this.perfilAbierto = !this.perfilAbierto;
  }

  @HostListener('document:click')
  cerrarPerfil() {
    this.perfilAbierto = false;
  }

  logout() {
    localStorage.removeItem('adminLogueado');
    this.actualizarEstadoLogin();
    this.perfilAbierto = false;
    this.router.navigate(['/login-admin']);
  }

  goToLogin() {
    this.router.navigate(['/login-admin']);
  }
}