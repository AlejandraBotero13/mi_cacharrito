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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mi_cacharrito.modelo.Reserva;
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
public ResponseEntity<?> crearReserva(@RequestParam("ccUsuario") String cc, @RequestParam("numAsiento") int asiento, @RequestParam("idViaje") int idViaje) {
    Optional<Usuario> userOpt = repositorioUsuario.findById(cc);
    Optional<Viaje> viajeOpt = repositorioViaje.findById(idViaje);
    
    if (userOpt.isEmpty() || viajeOpt.isEmpty()) {
        return ResponseEntity.status(404).body("Usuario o Viaje no encontrado");
    }
    
    Usuario usuario = userOpt.get();
    Viaje viaje = viajeOpt.get();
    
    Reserva reserva = new Reserva();
    reserva.setNumeroAsiento(asiento);
    reserva.setFechaReserva(LocalDateTime.now());
    reserva.setEstado(Reserva.EstadoReserva.pendiente);
    reserva.setUsuario(usuario);
    reserva.setViaje(viaje);
    reserva.setAdministrador(null);
    reserva.setTotalPagar(viaje.getPrecio());
    
    repositorioReserva.save(reserva);
    return ResponseEntity.ok(reserva);
}

    @PutMapping("/actualizarReserva")
    public String actualizarReserva(@RequestBody Reserva reserva) {
        if (!repositorioReserva.existsById(reserva.getId()))
            return "Reserva no existe";
        repositorioReserva.save(reserva);
        return "Reserva actualizada";
    }

    @GetMapping("/consultarReservas")
    public List<Reserva> consultarReservas(@RequestParam("cc") String cc) {
        return repositorioReserva.findByUsuario_Cc(cc);
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



