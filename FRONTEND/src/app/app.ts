import { Component, signal } from '@angular/core';
import { Navegacion } from './Navegacion/navegacion/navegacion';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Navegacion],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('app');
}