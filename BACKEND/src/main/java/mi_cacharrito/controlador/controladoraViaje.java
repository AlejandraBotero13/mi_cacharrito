package mi_cacharrito.controlador;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mi_cacharrito.modelo.Automovil;
import mi_cacharrito.modelo.Itinerario;
import mi_cacharrito.modelo.Viaje;
import mi_cacharrito.modelo.Viaje.EstadoViaje;
import mi_cacharrito.repositorio.automovil;
import mi_cacharrito.repositorio.itinerario;
import mi_cacharrito.repositorio.viaje;

@RestController
@RequestMapping("/viajes/v/")
@CrossOrigin(origins = "http://localhost:4200")
public class controladoraViaje {

    @Autowired
    private viaje repositorioViaje;

    @Autowired
    private automovil repositorioAutomovil;

    @Autowired
    private itinerario repositorioItinerario;

    @PostMapping("/crearViaje")
    public ResponseEntity<?> crearViaje(@RequestParam("fecha") String fecha,@RequestParam("horaSalida") String horaSalida,@RequestParam("precio") BigDecimal precio,@RequestParam("lugarSalida") String lugarSalida) {
        Viaje v = new Viaje();
        v.setFecha(LocalDate.parse(fecha));
        v.setHoraSalida(LocalTime.parse(horaSalida));
        v.setPrecio(precio);
        v.setEstado(EstadoViaje.activo);
        v.setLugarSalida(lugarSalida);
        repositorioViaje.save(v);
        return ResponseEntity.ok(v);
    }

    @GetMapping("/listarViajes")
    public List<Viaje> listarViajes() {
        return repositorioViaje.findAll();
    }

    
    @DeleteMapping("/eliminarViaje")
    public ResponseEntity<String> eliminarViaje(@RequestParam("id") int id) {
        if (!repositorioViaje.existsById(id))
            return ResponseEntity.status(404).body("No existe viaje con id: " + id);
        try {
            repositorioViaje.deleteById(id);
            return ResponseEntity.ok("Viaje eliminado");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("No se puede eliminar: el viaje tiene registros asociados");
        }
    }

    @PostMapping("/programar")
    public String programar(@RequestParam("id") int id,@RequestParam("fecha") String fecha,@RequestParam("horaSalida") String horaSalida) {
        Optional<Viaje> opt = repositorioViaje.findById(id);
        if (opt.isEmpty()) return "Viaje no existe";
        Viaje v = opt.get();
        v.setFecha(LocalDate.parse(fecha));
        v.setHoraSalida(LocalTime.parse(horaSalida));
        repositorioViaje.save(v);
        return "Viaje reprogramado";
    }

    @PostMapping("/cambiarEstado")
    public String cambiarEstado(@RequestParam("id") int id,@RequestParam("estado") String estado) {
        Optional<Viaje> opt = repositorioViaje.findById(id);
        if (opt.isEmpty()) return "Viaje no existe";
        Viaje v = opt.get();
        v.setEstado(EstadoViaje.valueOf(estado.toLowerCase()));
        repositorioViaje.save(v);
        return "Estado actualizado a " + estado;
    }

    @PostMapping("/asignarAutomovil")
    public String asignarAutomovil(@RequestParam("idViaje") int idViaje,@RequestParam("idAuto") int idAuto) {
        Optional<Viaje> vOpt = repositorioViaje.findById(idViaje);
        Optional<Automovil> aOpt = repositorioAutomovil.findById(idAuto);
        if (vOpt.isEmpty() || aOpt.isEmpty())
            return "Viaje o Automóvil no existe";
        Viaje v = vOpt.get();
        v.setAutomovil(aOpt.get());
        repositorioViaje.save(v);
        return "Automóvil asignado al viaje";
    }

    @GetMapping("/obtenerItinerario")
    public ResponseEntity<?> obtenerItinerario(@RequestParam("idViaje") int idViaje) {
        List<Itinerario> lista = repositorioItinerario.findByViaje_Id(idViaje);
        if (lista.isEmpty())
            return ResponseEntity.status(404).body("El viaje no tiene itinerario");
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/actualizarViaje")
    public ResponseEntity<?> actualizarViaje(@RequestParam("id") int id,@RequestParam("fecha") String fecha,@RequestParam("horaSalida") String horaSalida,@RequestParam("precio") BigDecimal precio,@RequestParam("lugarSalida") String lugarSalida,@RequestParam(value = "estado", required = false) String estado) {

        Optional<Viaje> opt = repositorioViaje.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("Viaje no existe");
        }
        Viaje v = opt.get();
        v.setFecha(LocalDate.parse(fecha));
        v.setHoraSalida(LocalTime.parse(horaSalida));
        v.setPrecio(precio);
        v.setLugarSalida(lugarSalida);
        if (estado != null) {
            try {
                v.setEstado(Viaje.EstadoViaje.valueOf(estado.toLowerCase()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(400).body("Estado inválido (activo, cancelado, finalizado)");
            }
        }
        repositorioViaje.save(v);
        return ResponseEntity.ok(v);
    }

    @GetMapping("/idYPlaca")
    public List<Map<String, Object>> idYPlaca() {
        List<Object[]> resultado = repositorioViaje.findIdYPlacaAutomovil();
        List<Map<String, Object>> lista = new ArrayList<>();
        for (Object[] row : resultado) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);
            map.put("placa", row[1]);
            lista.add(map);
        }
        return lista;
    }

    @GetMapping("/buscarViaje")
        public ResponseEntity<?> buscarViaje(@RequestParam("id") int id) {
        Optional<Viaje> opt = repositorioViaje.findById(id);
        if (opt.isEmpty()) return ResponseEntity.status(404).body("Viaje no encontrado");
        return ResponseEntity.ok(opt.get());
    }
    
}