package mi_cacharrito.controlador;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

import mi_cacharrito.modelo.Reserva;
import mi_cacharrito.modelo.Reserva.EstadoReserva;
import mi_cacharrito.modelo.Usuario;
import mi_cacharrito.modelo.Viaje;
import mi_cacharrito.repositorio.reserva;
import mi_cacharrito.repositorio.usuario;
import mi_cacharrito.repositorio.viaje;

@RestController
@RequestMapping("/reservas/r/")
@CrossOrigin(origins = "http://localhost:4200")
public class controladoraReserva {

    @Autowired
    private reserva repositorioReserva;

    @Autowired
    private usuario repositorioUsuario;

    @Autowired
    private viaje repositorioViaje;

    @PostMapping("/crearReserva")
    public ResponseEntity<?> crearReserva(@RequestParam("numAsiento") int numAsiento, @RequestParam("idViaje") int idViaje, @RequestParam("ccUsuario") String ccUsuario) {

        Optional<Viaje> viajeOpt = repositorioViaje.findById(idViaje);
        Optional<Usuario> usuarioOpt = repositorioUsuario.findById(ccUsuario);

        if (viajeOpt.isEmpty() || usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Viaje o Usuario no encontrado");
        }

        Viaje viaje = viajeOpt.get();
        Usuario usuario = usuarioOpt.get();

        if (viaje.getEstado() != Viaje.EstadoViaje.activo) {
            return ResponseEntity.status(400).body("No se puede reservar en un viaje que no está activo");
        }

        if (viaje.getAutomovil() == null) {
            return ResponseEntity.status(400).body("El viaje no tiene un automóvil asignado. No se puede reservar.");
        }

        int capacidad = viaje.getAutomovil().getCapacidad();
        if (numAsiento < 1 || numAsiento > capacidad) {
            return ResponseEntity.status(400).body("El número de asiento debe estar entre 1 y " + capacidad);
        }

        if (repositorioReserva.existsByViajeYAsiento(idViaje, numAsiento)) {
            return ResponseEntity.status(409).body("El asiento " + numAsiento + " ya está reservado para este viaje");
        }

        Reserva r = new Reserva();
        r.setNumeroAsiento(numAsiento);
        r.setFechaReserva(LocalDateTime.now());
        r.setEstado(EstadoReserva.pendiente);
        r.setUsuario(usuario);
        r.setViaje(viaje);
        r.setAdministrador(null);
        r.setTotalPagar(viaje.getPrecio());

        repositorioReserva.save(r);
        return ResponseEntity.ok(r);
    }

    @GetMapping("/listarReservas")
    public List<Reserva> listarReservas() {
        return repositorioReserva.findAll();
    }

    @DeleteMapping("/eliminarReserva")
    public String eliminarReserva(@RequestParam("id") int id) {
        if (!repositorioReserva.existsById(id))
            return "No existe reserva con id: " + id;
        repositorioReserva.deleteById(id);
        return "Reserva eliminada";
    }

    @GetMapping("/calcularTotal")
    public ResponseEntity<?> calcularTotal(@RequestParam("id") int id) {
        BigDecimal total = repositorioReserva.calcularTotal(id);
        if (total == null) return ResponseEntity.status(404).body("Reserva no encontrada");
        return ResponseEntity.ok("Total a pagar: " + total);
    }

    @PostMapping("/confirmarPago")
    public String confirmarPago(@RequestParam("id") int id) {
        Optional<Reserva> opt = repositorioReserva.findById(id);
        if (opt.isEmpty()) return "Reserva no existe";
        Reserva r = opt.get();
        r.setEstado(EstadoReserva.pagada);
        repositorioReserva.save(r);
        return "Pago confirmado, reserva pagada";
    }

    @GetMapping("/consultarReserva")
    public ResponseEntity<?> consultarReserva(@RequestParam("id") int id) {
        Optional<Reserva> r = repositorioReserva.findById(id);
        if (r.isPresent()) return ResponseEntity.ok(r.get());
        return ResponseEntity.status(404).body("Reserva no encontrada");
    }

    @PostMapping("/cancelarReserva")
    public String cancelarReserva(@RequestParam("id") int id) {
        Optional<Reserva> opt = repositorioReserva.findById(id);
        if (opt.isEmpty()) return "Reserva no existe";
        Reserva r = opt.get();
        r.setEstado(EstadoReserva.cancelada);
        repositorioReserva.save(r);
        return "Reserva cancelada";
    }
}