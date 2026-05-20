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

import mi_cacharrito.modelo.Automovil;
import mi_cacharrito.modelo.Viaje;
import mi_cacharrito.repositorio.automovil;
import mi_cacharrito.repositorio.viaje;

@RestController
@RequestMapping("/automoviles/au/")
@CrossOrigin(origins = "http://localhost:4200")
public class controladoraAutomovil {

    @Autowired
    private automovil repositorioAutomovil;

    @Autowired
    private viaje repositorioViaje;

    @GetMapping("/listar")
    public List<Automovil> listarAutomoviles() {
        return repositorioAutomovil.findAll();
    }

    @GetMapping("/buscarId")
    public ResponseEntity<?> buscarPorId(@RequestParam("id") int id) {
        Optional<Automovil> auto = repositorioAutomovil.findById(id);
        if (auto.isPresent()) {
            return ResponseEntity.ok(auto.get());
        } else {
            return ResponseEntity.status(404).body("No existe automóvil con id: " + id);
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearAutomovil(@RequestParam("placa") String placa, @RequestParam("capacidad") int capacidad, @RequestParam("modelo") int modelo, @RequestParam("marca") String marca) {
        Automovil a = new Automovil();
        a.setPlaca(placa);
        a.setCapacidad(capacidad);
        a.setModelo(modelo);
        a.setMarca(marca);
        repositorioAutomovil.save(a);
        return ResponseEntity.ok(a);
    }

    @DeleteMapping("/eliminar")
    public String eliminarAutomovil(@RequestParam("id") int id) {
        if (!repositorioAutomovil.existsById(id)) {
            return "No existe automóvil con id: " + id;
        }
        repositorioAutomovil.deleteById(id);
        return "Automóvil eliminado";
    }

    @GetMapping("/capacidadDisponible")
    public ResponseEntity<?> consultarCapacidadDisponible(@RequestParam("id") int id) {
        Optional<Automovil> auto = repositorioAutomovil.findById(id);
        if (auto.isEmpty()) {
            return ResponseEntity.status(404).body("Automóvil no encontrado");
        }
        Automovil a = auto.get();
        int capacidadTotal = a.getCapacidad();
        return ResponseEntity.ok("Capacidad total: " + capacidadTotal);
    }

    @PostMapping("/asignarViaje")
    public ResponseEntity<?> asignarViaje(@RequestParam("idAuto") int idAuto, @RequestParam("idViaje") int idViaje) {
        Optional<Automovil> auto = repositorioAutomovil.findById(idAuto);
        Optional<Viaje> viaje = repositorioViaje.findById(idViaje);
        if (auto.isEmpty() || viaje.isEmpty()) {
            return ResponseEntity.status(404).body("Automóvil o Viaje no encontrado");
        }
        Viaje v = viaje.get();
        v.setAutomovil(auto.get());
        repositorioViaje.save(v);
        return ResponseEntity.ok("Viaje asignado al automóvil correctamente");
    }

    @PostMapping("/actualizarAutomovil")
    public ResponseEntity<?> actualizarAutomovil(@RequestParam("id") int id, @RequestParam("placa") String placa,@RequestParam("capacidad") int capacidad,@RequestParam("modelo") int modelo,@RequestParam("marca") String marca) {
        Optional<Automovil> opt = repositorioAutomovil.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("Automóvil no existe");
    }
    Automovil auto = opt.get();
    auto.setPlaca(placa);
    auto.setCapacidad(capacidad);
    auto.setModelo(modelo);
    auto.setMarca(marca);
    repositorioAutomovil.save(auto);
    return ResponseEntity.ok(auto);
}
}