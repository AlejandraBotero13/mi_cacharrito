package mi_cacharrito.controlador;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/listarReservas")
    public List<Reserva> listarReservas() {
        return repositorioReserva.findAll();
    }

    @GetMapping("/consultarReserva")
    public ResponseEntity<?> consultarReserva(@RequestParam("id") int id) {
        Optional<Reserva> r = repositorioReserva.findById(id);
        return r.isPresent() ? ResponseEntity.ok(r.get())
                : ResponseEntity.status(404).body("Reserva no encontrada");
    }

    @PostMapping("/crearReserva")
    public ResponseEntity<?> crearReserva(@RequestParam("numAsiento") int numAsiento, @RequestParam("idViaje") int idViaje, 
    @RequestParam("ccUsuario") String ccUsuario) {

        return crearReservaBase(numAsiento, idViaje, ccUsuario, null);
    }

    @PostMapping("/crearReservaConAdmin")
    public ResponseEntity<?> crearReservaConAdmin(@RequestParam("numAsiento") int numAsiento, @RequestParam("idViaje") int idViaje,@RequestParam("ccUsuario") String ccUsuario, @RequestParam("idAdmin") int idAdmin) {

        Optional<Administrador> adminOpt = repositorioAdministrador.findById(idAdmin);
        if (adminOpt.isEmpty())
            return ResponseEntity.status(404).body("Administrador no encontrado");
        return crearReservaBase(numAsiento, idViaje, ccUsuario, adminOpt.get());
    }

    private ResponseEntity<?> crearReservaBase(int numAsiento, int idViaje, String ccUsuario, Administrador admin) {
        Optional<Viaje> viajeOpt = repositorioViaje.findById(idViaje);
        Optional<Usuario> usuarioOpt = repositorioUsuario.findById(ccUsuario);
        if (viajeOpt.isEmpty() || usuarioOpt.isEmpty())
            return ResponseEntity.status(404).body("Viaje o Usuario no encontrado");

        Viaje viaje = viajeOpt.get();
        Usuario usuario = usuarioOpt.get();

        if (viaje.getEstado() != Viaje.EstadoViaje.activo)
            return ResponseEntity.status(400).body("Viaje no activo");
        if (viaje.getAutomovil() == null)
            return ResponseEntity.status(400).body("Viaje sin automóvil");

        int capacidad = viaje.getAutomovil().getCapacidad();
        if (numAsiento < 1 || numAsiento > capacidad)
            return ResponseEntity.status(400).body("Asiento inválido (1-" + capacidad + ")");
        if (repositorioReserva.existsByViajeYAsiento(idViaje, numAsiento))
            return ResponseEntity.status(409).body("Asiento ocupado");

        Reserva r = new Reserva();
        r.setNumeroAsiento(numAsiento);
        r.setFechaReserva(LocalDateTime.now());
        r.setEstado(EstadoReserva.pendiente);
        r.setUsuario(usuario);
        r.setViaje(viaje);
        r.setAdministrador(admin);
        r.setTotalPagar(viaje.getPrecio());
        repositorioReserva.save(r);
        return ResponseEntity.ok(r);
    }

    @PostMapping("/actualizarReserva")
    public ResponseEntity<?> actualizarReserva(@RequestParam("id") int id, @RequestParam(value = "numAsiento", required = false) Integer numAsiento, @RequestParam(value = "idViaje", required = false) Integer idViaje, @RequestParam(value = "estado", required = false)  String estado) {
        Optional<Reserva> opt = repositorioReserva.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.status(404).body("Reserva no existe");
        Reserva r = opt.get();

        if (numAsiento != null) {
            Viaje viajeActual = r.getViaje();
            if (viajeActual != null) {
                boolean ocupado = repositorioReserva.existsByViajeYAsientoActualizar(viajeActual.getId(), numAsiento, id);
                if (ocupado)
                    return ResponseEntity.status(409).body("Asiento " + numAsiento + " ocupado");
            }
            r.setNumeroAsiento(numAsiento);
        }
        if (idViaje != null) {
            Optional<Viaje> nuevoViaje = repositorioViaje.findById(idViaje);
            if (nuevoViaje.isEmpty())
                return ResponseEntity.status(404).body("Viaje no existe");
            r.setViaje(nuevoViaje.get());
            r.setTotalPagar(nuevoViaje.get().getPrecio());
        }
        if (estado != null) {
            try {
                r.setEstado(Reserva.EstadoReserva.valueOf(estado));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(400).body("Estado inválido");
            }
        }
        repositorioReserva.save(r);
        return ResponseEntity.ok(r);
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

    @DeleteMapping("/eliminarReserva")
    public String eliminarReserva(@RequestParam("id") int id) {
        if (!repositorioReserva.existsById(id))
            return "Reserva no existe";
        repositorioReserva.deleteById(id);
        return "Reserva eliminada";
    }

    @GetMapping("/calcularTotal")
    public ResponseEntity<?> calcularTotal(@RequestParam("id") int id) {
        BigDecimal total = repositorioReserva.calcularTotal(id);
        if (total == null) return ResponseEntity.status(404).body("Reserva no encontrada");
        return ResponseEntity.ok("Total a pagar: " + total);
    }

    @GetMapping("/elegirDestinoYFecha")
    public List<Viaje> elegirDestinoYFecha(@RequestParam("destinoId") int destinoId, @RequestParam("fecha") String fecha) {
        LocalDate fechaParsed = LocalDate.parse(fecha);
        return repositorioViaje.findByFechaYDestino(fechaParsed, destinoId);
    }

    @GetMapping("/verDisponibilidad")
    public ResponseEntity<?> verDisponibilidad(@RequestParam("idViaje") int idViaje) {

        Optional<Viaje> viajeOpt = repositorioViaje.findById(idViaje);
        if (viajeOpt.isEmpty())
            return ResponseEntity.status(404).body("Viaje no existe");
        Viaje viaje = viajeOpt.get();
        if (viaje.getAutomovil() == null)
            return ResponseEntity.status(400).body("Viaje sin automóvil");
        int capacidad = viaje.getAutomovil().getCapacidad();
        List<Integer> asientosOcupados = repositorioReserva.findByViajeId(idViaje).stream().filter(r -> r.getEstado() == EstadoReserva.pendiente || r.getEstado() == EstadoReserva.pagada).map(Reserva::getNumeroAsiento).collect(Collectors.toList());
        List<Integer> asientosLibres = new ArrayList<>();
        for (int i = 1; i <= capacidad; i++) {
            if (!asientosOcupados.contains(i))
                asientosLibres.add(i);
        }
        return ResponseEntity.ok(asientosLibres);
    }

    @PostMapping("/confirmarReserva")
    public ResponseEntity<?> confirmarReserva(@RequestParam("idReserva") int idReserva) {

        Optional<Reserva> opt = repositorioReserva.findById(idReserva);
        if (opt.isEmpty())
            return ResponseEntity.status(404).body("Reserva no existe");
        Reserva r = opt.get();
        if (r.getEstado() != EstadoReserva.pendiente)
            return ResponseEntity.status(400).body("Solo pendiente puede confirmarse");
       
        r.setEstado(EstadoReserva.pagada);
        repositorioReserva.save(r);
        return ResponseEntity.ok("Reserva confirmada. Asiento: " + r.getNumeroAsiento());
    }
}