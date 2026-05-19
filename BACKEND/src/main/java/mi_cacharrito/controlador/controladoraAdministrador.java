package mi_cacharrito.controlador;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import mi_cacharrito.modelo.Administrador;
import mi_cacharrito.modelo.Reserva;
import mi_cacharrito.modelo.Viaje;
import mi_cacharrito.repositorio.administrador;
import mi_cacharrito.repositorio.reserva;
import mi_cacharrito.repositorio.viaje;

@RestController
@RequestMapping("/administradores/a/")
@CrossOrigin(origins = "http://localhost:4200")
public class controladoraAdministrador {

    @Autowired 
    private administrador repositorioAdministrador;

    @Autowired 
    private viaje repositorioViaje;

    @Autowired 
    private reserva repositorioReserva;



    @GetMapping("/listar")
    public List<Administrador> listarAdministradores() {
        return repositorioAdministrador.findAll();
    }

    @GetMapping("/buscarId")
    public ResponseEntity<?> buscarAdministradorPorId(@RequestParam("id") int id) {
        Optional<Administrador> admin = repositorioAdministrador.findById(id);
        return admin.isPresent() ? ResponseEntity.ok(admin.get())
                : ResponseEntity.status(404).body("No existe administrador con id: " + id);
    }

    @PostMapping("/guardar")
    public ResponseEntity<Administrador> guardarAdministrador(@RequestBody Administrador admin) {
        repositorioAdministrador.save(admin);
        return ResponseEntity.ok(admin);
    }

   @DeleteMapping("/eliminar")
    public String eliminarAdministrador(@RequestParam("id") int id) {
        if (!repositorioAdministrador.existsById(id)) {
            return "No existe administrador con id: " + id;
        }

        List<Reserva> reservas = repositorioReserva.findByAdministradorId(id);
        if (!reservas.isEmpty()) {
           
            for (Reserva r : reservas) {
                r.setAdministrador(null);
                repositorioReserva.save(r);
            }
           
        }

        repositorioAdministrador.deleteById(id);
        return "Administrador eliminado y sus reservas han quedado sin administrador asociado.";
    }

    @PostMapping("/iniciarSesion")
    public ResponseEntity<?> iniciarSesion(@RequestParam("usuario") String usuario, @RequestParam("contraseña") String contraseña) {
        List<Administrador> admins = repositorioAdministrador.findByUsuario(usuario);
        for (Administrador a : admins) {
            if (a.getContraseña().equals(contraseña)) {
                return ResponseEntity.ok(a);
            }
        }
        return ResponseEntity.status(401).body("Usuario o contraseña incorrectos");
    }


    @GetMapping("/reservasDelDia")
    public List<Reserva> listarReservasDelDia() {
        LocalDate hoy = LocalDate.now();
        return repositorioReserva.findAll().stream()
                .filter(r -> r.getFechaReserva().toLocalDate().equals(hoy))
                .collect(Collectors.toList());
    }

    @PostMapping("/cancelarReservacion")
    public String cancelarReservacion(@RequestParam("idReserva") int idReserva) {
        Optional<Reserva> opt = repositorioReserva.findById(idReserva);
        if (opt.isEmpty()) return "Reserva no existe";
        Reserva r = opt.get();
        r.setEstado(Reserva.EstadoReserva.cancelada);
        repositorioReserva.save(r);
        return "Reserva cancelada";
    }

    @PostMapping("/modificarReservacion")
    public ResponseEntity<?> modificarReservacion(@RequestParam("idReserva") int idReserva,@RequestParam(value = "numAsiento", required = false) Integer numAsiento,@RequestParam(value = "idViaje", required = false) Integer idViaje,@RequestParam(value = "estado", required = false) String estado) {

        Optional<Reserva> opt = repositorioReserva.findById(idReserva);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("Reserva no existe");
        }
        Reserva r = opt.get();

        if (numAsiento != null) {
            Viaje viajeActual = r.getViaje();
            if (viajeActual != null) {
                boolean ocupado = repositorioReserva.existsByViajeYAsientoActualizar(
                        viajeActual.getId(), numAsiento, idReserva);
                if (ocupado) {
                    return ResponseEntity.status(409).body("El asiento " + numAsiento + " ya está ocupado");
                }
            }
            r.setNumeroAsiento(numAsiento);
        }

        if (idViaje != null) {
            Optional<Viaje> viajeOpt = repositorioViaje.findById(idViaje);
            if (viajeOpt.isEmpty()) {
                return ResponseEntity.status(404).body("El nuevo viaje no existe");
            }
            Viaje nuevoViaje = viajeOpt.get();
            r.setViaje(nuevoViaje);
            r.setTotalPagar(nuevoViaje.getPrecio());
        }

        if (estado != null) {
            try {
                r.setEstado(Reserva.EstadoReserva.valueOf(estado));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(400).body("Estado inválido. Valores permitidos: pendiente, pagada, finalizada, cancelada");
            }
        }

        repositorioReserva.save(r);
        return ResponseEntity.ok(r);
    }

    @PostMapping("/registrarPago")
    public String registrarPago(@RequestParam("idReserva") int idReserva) {
        Optional<Reserva> opt = repositorioReserva.findById(idReserva);
        if (opt.isEmpty()) return "Reserva no existe";
        Reserva r = opt.get();
        if (r.getEstado() != Reserva.EstadoReserva.pendiente) {
            return "La reserva no está en un estado que permita el pago (debe estar pendiente)";
        }
        r.setEstado(Reserva.EstadoReserva.pagada);
        repositorioReserva.save(r);
        return "Pago registrado exitosamente";
    }
}
