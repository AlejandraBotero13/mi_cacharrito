package mi_cacharrito.controlador;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequestMapping("/usuarios/u/")
@CrossOrigin(origins = "http://localhost:4200")

public class controladoraUsuario {


    @Autowired
    private usuario repositorioUsuario;

    @Autowired
    private reserva repositorioReserva;

    @Autowired
    private viaje repositorioViaje;

    @GetMapping("/listar")
    public List<Usuario> listar() {
        return repositorioUsuario.findAll();
    }

    @GetMapping("/buscarCc")
    public ResponseEntity<?> buscarPorCc(@RequestParam("cc") String cc) {
        Optional<Usuario> u = repositorioUsuario.findById(cc);
        if (u.isPresent()) return ResponseEntity.ok(u.get());
        return ResponseEntity.status(404).body("Usuario no encontrado");
    }

    @PostMapping("/guardar")
    public ResponseEntity<Usuario> guardar(@RequestBody Usuario usuario) {
        repositorioUsuario.save(usuario);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/eliminar")
    public String eliminar(@RequestParam("cc") String cc) {
        if (!repositorioUsuario.existsById(cc)) {
            return "No existe usuario con cc: " + cc;
        }
        List<Reserva> reservas = repositorioReserva.findByUsuarioCc(cc);
        if (!reservas.isEmpty()) {
            return "No se puede eliminar el usuario porque tiene " + reservas.size() + " reservas asociadas. Cancelélas primero.";
        }
        repositorioUsuario.deleteById(cc);
        return "Usuario eliminado";
    }

    
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

    @PostMapping("/actualizarReserva")
    public ResponseEntity<?> actualizarReserva(@RequestParam("id") int id, @RequestParam(value = "numAsiento", required = false) Integer numAsiento, @RequestParam(value = "idViaje", required = false) Integer idViaje, @RequestParam(value = "estado", required = false) String estado) {

        Optional<Reserva> reservaOpt = repositorioReserva.findById(id);
        if (reservaOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Reserva no existe");
        }
        Reserva reservaExistente = reservaOpt.get();

        boolean asientoCambiado = false;
        boolean viajeCambiado = false;
        int nuevoAsiento = reservaExistente.getNumeroAsiento();
        Integer nuevoIdViaje = reservaExistente.getViaje() != null ? reservaExistente.getViaje().getId() : null;

        if (numAsiento != null && numAsiento != reservaExistente.getNumeroAsiento()) {
            nuevoAsiento = numAsiento;
            asientoCambiado = true;
        }
        if (idViaje != null && (reservaExistente.getViaje() == null || !idViaje.equals(reservaExistente.getViaje().getId()))) {
            nuevoIdViaje = idViaje;
            viajeCambiado = true;
        }
        if (estado != null) {
            try {
                reservaExistente.setEstado(Reserva.EstadoReserva.valueOf(estado));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(400).body("Estado inválido. Valores permitidos: pendiente, pagada, finalizada, cancelada");
            }
        }

        if (asientoCambiado || viajeCambiado) {
            Viaje viajeValidar;
            if (viajeCambiado) {
                Optional<Viaje> viajeOpt = repositorioViaje.findById(nuevoIdViaje);
                if (viajeOpt.isEmpty()) {
                    return ResponseEntity.status(404).body("El nuevo viaje no existe");
                }
                viajeValidar = viajeOpt.get();
            } else {
                viajeValidar = reservaExistente.getViaje();
                if (viajeValidar == null) {
                    return ResponseEntity.status(400).body("La reserva original no tiene un viaje asociado válido");
                }
            }

            if (viajeValidar.getEstado() != Viaje.EstadoViaje.activo) {
                return ResponseEntity.status(400).body("No se puede modificar la reserva porque el viaje no está activo");
            }

            if (viajeValidar.getAutomovil() == null) {
                return ResponseEntity.status(400).body("El viaje no tiene un automóvil asignado.");
            }

            int capacidad = viajeValidar.getAutomovil().getCapacidad();
            if (nuevoAsiento < 1 || nuevoAsiento > capacidad) {
                return ResponseEntity.status(400).body("Este automovil solo tiene " + capacidad + " puestos.");
            }

            boolean ocupado = repositorioReserva.existsByViajeYAsientoActualizar(
                    viajeValidar.getId(), nuevoAsiento, id);
            if (ocupado) {
                return ResponseEntity.status(409).body("El puesto " + nuevoAsiento + " ya está ocupado para este viaje");
            }

            reservaExistente.setNumeroAsiento(nuevoAsiento);
            if (viajeCambiado) {
                reservaExistente.setViaje(viajeValidar);
            }
        }

        repositorioReserva.save(reservaExistente);
        return ResponseEntity.ok(reservaExistente);
    }

    @GetMapping("/consultarReservas")
    public List<Reserva> consultarReservas(@RequestParam("cc") String cc) {
        return repositorioReserva.findByUsuarioCc(cc);
    }

    @DeleteMapping("/cancelarReserva")
    public String cancelarReserva(@RequestParam("idReserva") int idReserva) {
        Optional<Reserva> r = repositorioReserva.findById(idReserva);
        if (r.isEmpty()) return "Reserva no existe";
        Reserva reserva = r.get();
        reserva.setEstado(Reserva.EstadoReserva.cancelada);
        repositorioReserva.save(reserva);
        return "Reserva cancelada";
    }
}



