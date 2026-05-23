import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-navegacion',
  imports: [RouterOutlet, CommonModule],
  templateUrl: './navegacion.html',
  styleUrl: './navegacion.css',
})
export class Navegacion implements OnInit {
  isLoggedIn = false;

  constructor(private router: Router) {}

  ngOnInit() {
    this.actualizarEstadoLogin();
    // Actualizar el estado cuando cambie la ruta (por si vuelves de logout)
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.actualizarEstadoLogin();
      }
    });
  }

  actualizarEstadoLogin() {
    const admin = localStorage.getItem('adminLogueado');
    this.isLoggedIn = !!admin;
  }

  logout() {
    localStorage.removeItem('adminLogueado');
    this.actualizarEstadoLogin();
    this.router.navigate(['/login-admin']);
  }

  goToLogin() {
    this.router.navigate(['/login-admin']);
  }
}