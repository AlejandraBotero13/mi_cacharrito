package mi_cacharrito.controlador;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mi_cacharrito.modelo.Destino;
import mi_cacharrito.modelo.Itinerario;
import mi_cacharrito.modelo.Viaje;
import mi_cacharrito.repositorio.destino;
import mi_cacharrito.repositorio.itinerario;
import mi_cacharrito.repositorio.viaje;

@RestController
@RequestMapping("/itinerarios/i/")
@CrossOrigin(origins = "*")
public class controladoraItinerario {

    @Autowired
    private itinerario repositorioItinerario;

    @Autowired
    private viaje repositorioViaje;

    @Autowired
    private destino repositorioDestino;

    @PostMapping("/crearItinerario")
    public ResponseEntity<?> crearItinerario(@RequestParam("idViaje") int idViaje, @RequestParam("idDestino") int idDestino, @RequestParam("orden") short orden) {
        Optional<Viaje> viaje = repositorioViaje.findById(idViaje);
        Optional<Destino> destino = repositorioDestino.findById(idDestino);
        if (viaje.isEmpty() || destino.isEmpty()) {
            return ResponseEntity.status(404).body("Viaje o Destino no existe");
        }
        Itinerario it = new Itinerario(orden, destino.get(), viaje.get());
        repositorioItinerario.save(it);
        return ResponseEntity.ok(it);
    }



    @PostMapping("/agregarDestino")
    public ResponseEntity<?> agregarDestino(@RequestParam("idViaje") int idViaje, @RequestParam("idDestino") int idDestino, @RequestParam("orden") short orden) {
        Optional<Viaje> viaje = repositorioViaje.findById(idViaje);
        Optional<Destino> destino = repositorioDestino.findById(idDestino);
        if (viaje.isEmpty() || destino.isEmpty()) {
            return ResponseEntity.status(404).body("Viaje o Destino no encontrado");
        }
        Itinerario it = new Itinerario(orden, destino.get(), viaje.get());
        repositorioItinerario.save(it);
        return ResponseEntity.ok(it);
    }


    @GetMapping("/listarDestinos")
    public ResponseEntity<?> listarDestinos(@RequestParam("idViaje") int idViaje) {
        List<Itinerario> lista = repositorioItinerario.findByViaje_Id(idViaje);
        if (lista.isEmpty()) {
            return ResponseEntity.status(404).body("No hay destinos para este viaje");
        }
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/eliminarDestino")
    public String eliminarDestino(@RequestParam("idViaje") int idViaje, @RequestParam("orden") short orden) {
        List<Itinerario> lista = repositorioItinerario.findByViaje_Id(idViaje);
        for (Itinerario it : lista) {
            if (it.getOrdenVisita() == orden) {
                repositorioItinerario.delete(it);
                return "Destino eliminado del itinerario";
            }
        }
        return "No existe destino con esa orden en el viaje";
    }

    @PostMapping("/actualizarItinerario")
    public String actualizarItinerario(@RequestParam("idViaje") int idViaje, @RequestParam("ordenActual") short ordenActual, @RequestParam("nuevoOrden") short nuevoOrden) {
        List<Itinerario> lista = repositorioItinerario.findByViaje_Id(idViaje);
        Itinerario it = null;
        for (Itinerario i : lista) {
            if (i.getOrdenVisita() == ordenActual) {
                it = i;
                break;
            }
        }
        if (it == null) return "No se encontró el destino con esa orden en el itinerario";

        for (Itinerario i : lista) {
            if (i.getOrdenVisita() == nuevoOrden) {
                return "Ya existe un destino con el orden " + nuevoOrden + " en este viaje. Intercambie órdenes o use otro número.";
            }
        }

        Viaje viaje = it.getViaje();
        Destino destino = it.getDestino();

        repositorioItinerario.delete(it);

        Itinerario nuevo = new Itinerario(nuevoOrden, destino, viaje);
        repositorioItinerario.save(nuevo);

        return "Itinerario actualizado: orden del destino modificado correctamente de " + ordenActual + " a " + nuevoOrden;
    }
    
    @GetMapping("/obtenerDestino")
    public ResponseEntity<?> obtenerDestino(@RequestParam("idViaje") int idViaje, @RequestParam("orden") short orden) {
        List<Itinerario> lista = repositorioItinerario.findByViaje_Id(idViaje);
        for (Itinerario it : lista) {
            if (it.getOrdenVisita() == orden) {
                return ResponseEntity.ok(it.getDestino());
            }
        }
        return ResponseEntity.status(404).body("No existe destino con esa orden");
    }

    @DeleteMapping("/eliminarItinerario")
    public String eliminarItinerario(@RequestParam("idViaje") int idViaje) {
        List<Itinerario> lista = repositorioItinerario.findByViaje_Id(idViaje);
        if (lista.isEmpty()) return "No hay itinerario para ese viaje";
        repositorioItinerario.deleteAll(lista);
        return "Itinerario completo eliminado";
    }

    @GetMapping("/listarOrdenado")
    public ResponseEntity<?> listarOrdenado() {
        return ResponseEntity.ok(repositorioItinerario.findAllOrdenadoPorViajeYOrden());
    }
}
