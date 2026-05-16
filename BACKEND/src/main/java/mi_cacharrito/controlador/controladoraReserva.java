package mi_cacharrito.controlador;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mi_cacharrito.modelo.Administrador;
import mi_cacharrito.modelo.Reserva;
import mi_cacharrito.modelo.Reserva.EstadoReserva;
import mi_cacharrito.modelo.Usuario;
import mi_cacharrito.modelo.Viaje;
import mi_cacharrito.repositorio.administrador;
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

    @Autowired
    private administrador repositorioAdministrador;

    @PostMapping("/crearReserva")
    public ResponseEntity<?> crearReserva(@RequestParam("numAsiento") int numAsiento, @RequestParam("idViaje") int idViaje, @RequestParam("ccUsuario") String ccUsuario, @RequestParam("idAdmin") int idAdmin) {
        Optional<Viaje> viaje = repositorioViaje.findById(idViaje);
        Optional<Usuario> usuario = repositorioUsuario.findById(ccUsuario);
        Optional<Administrador> admin = repositorioAdministrador.findById(idAdmin);
        if (viaje.isEmpty() || usuario.isEmpty() || admin.isEmpty()) {
            return ResponseEntity.status(404).body("Viaje, Usuario o Administrador no encontrado");
        }
        Reserva r = new Reserva();
        r.setNumeroAsiento(numAsiento);
        r.setFechaReserva(LocalDateTime.now());
        r.setEstado(EstadoReserva.pendiente);
        r.setUsuario(usuario.get());
        r.setViaje(viaje.get());
        r.setAdministrador(admin.get());
        r.setTotalPagar(viaje.get().getPrecio()); 
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