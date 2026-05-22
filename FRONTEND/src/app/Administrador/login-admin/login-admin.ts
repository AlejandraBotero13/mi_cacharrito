import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AdministradorServ } from '../../Servicio/administrador-serv';

@Component({
  selector: 'app-login-admin',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login-admin.html',
  styleUrls: ['./login-admin.css']
})
export class LoginAdmin {
  usuario = '';
  clave: string = '';
  error = '';

  constructor(private adminServ: AdministradorServ, private router: Router) {}

  iniciarSesion() {
    this.adminServ.login(this.usuario, this.clave).subscribe({
      next: (admin) => {
        localStorage.setItem('adminLogueado', JSON.stringify(admin));
        window.location.href = '/administradores';
      },
      error: () => this.error = 'Usuario o contraseña incorrectos'
    });
  }
}