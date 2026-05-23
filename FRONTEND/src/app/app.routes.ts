import { Routes } from '@angular/router';
import { Usuarios } from './Usuario/usuarios/usuarios';
import { Viajes } from './Viaje/viajes/viajes';
import { Reservas } from './Reserva/reservas/reservas';
import { Itinerarios } from './Itinerario/itinerarios/itinerarios';
import { Destinos } from './Destino/destinos/destinos';
import { Automoviles } from './Automovil/automoviles/automoviles';
import { Administradores } from './Administrador/administradores/administradores';
import { LoginAdmin } from './Administrador/login-admin/login-admin';


export const routes: Routes = [ 
    {path: '', redirectTo: 'reservas', pathMatch: 'full'},
    {path: 'usuarios', component: Usuarios},
    {path: 'viajes', component: Viajes},
    {path: 'reservas', component: Reservas},
    {path: 'itinerarios', component: Itinerarios},
    {path: 'destinos', component: Destinos},
    {path: 'automoviles', component: Automoviles},
    {path: 'administradores', component: Administradores},
    { path: 'login-admin', component: LoginAdmin }

];
