import { Component, signal } from '@angular/core';
import { Navegacion } from './Navegacion/navegacion/navegacion';

@Component({
  selector: 'app-root',
  imports: [Navegacion],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('app');
}