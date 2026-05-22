package mi_cacharrito.controlador;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mi_cacharrito.modelo.Administrador;
import mi_cacharrito.modelo.Automovil;
import mi_cacharrito.modelo.Destino;
import mi_cacharrito.modelo.Reserva;
import mi_cacharrito.modelo.Usuario;
import mi_cacharrito.modelo.Viaje;
import mi_cacharrito.repositorio.administrador;
import mi_cacharrito.repositorio.reserva;
import mi_cacharrito.repositorio.viaje;
import mi_cacharrito.util.EncryptionUtil;

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

    @Autowired 
    private controladoraViaje controladoraViaje;

    @Autowired 
    private controladoraAutomovil controladoraAutomovil;

    @Autowired 
    private controladoraDestino controladoraDestino;

    @Autowired 
    private controladoraItinerario controladoraItinerario;

    @Autowired 
    private controladoraReserva controladoraReserva;

    @Autowired 
    private controladoraUsuario controladoraUsuario;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired 
    private EncryptionUtil encryptionUtil;

    @GetMapping("/listar")
    public List<Administrador> listarAdministradores() {
        List<Administrador> admins = repositorioAdministrador.findAll();
        for (Administrador a : admins) {
            a.setUsuario(encryptionUtil.decrypt(a.getUsuario()));
        }
        return admins;
    }

    @GetMapping("/buscarId")
    public ResponseEntity<?> buscarAdministradorPorId(@RequestParam("id") int id) {
        Optional<Administrador> admin = repositorioAdministrador.findById(id);
        if (admin.isPresent()) {
            admin.get().setUsuario(encryptionUtil.decrypt(admin.get().getUsuario()));
            return ResponseEntity.ok(admin.get());
        } else {
            return ResponseEntity.status(404).body("No existe administrador con id: " + id);
        }
    }

    @PostMapping("/guardar")
    public ResponseEntity<Administrador> guardarAdministrador(@RequestBody Administrador admin) {
        admin.setUsuario(encryptionUtil.encrypt(admin.getUsuario()));
        admin.setContraseña(passwordEncoder.encode(admin.getContraseña()));
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
        String usuarioEncriptado = encryptionUtil.encrypt(usuario);
        List<Administrador> admins = repositorioAdministrador.findByUsuario(usuarioEncriptado);
        for (Administrador a : admins) {
            if (passwordEncoder.matches(contraseña, a.getContraseña())) {
                a.setUsuario(encryptionUtil.decrypt(a.getUsuario()));
                return ResponseEntity.ok(a);
            }
        }
        return ResponseEntity.status(401).body("Usuario o contraseña incorrectos");
    }

    @PostMapping("/crearUsuario")
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        return controladoraUsuario.guardar(usuario);
    }

    @GetMapping("/listarUsuario")
    public List<Usuario> listarUsuario() {
        return controladoraUsuario.listar();
    }

    @DeleteMapping("/eliminarUsuario")
    public String eliminarUsuario(@RequestParam("cc") String cc) {
        return controladoraUsuario.eliminar(cc);
    }

    @PostMapping("/actualizarUsuario")
    public ResponseEntity<Usuario> actualizarUsuario(@RequestBody Usuario usuario) {
        return controladoraUsuario.guardar(usuario);
    }

    @PostMapping("/crearReserva")
    public ResponseEntity<?> crearReserva(@RequestParam int numAsiento, @RequestParam int idViaje, @RequestParam String ccUsuario,@RequestParam int idAdmin) {
        return controladoraReserva.crearReservaConAdmin(numAsiento, idViaje, ccUsuario, idAdmin);
    }

    @GetMapping("/listarReservas")
    public List<Reserva> listarReservas() {
        return controladoraReserva.listarReservas();
    }

    @DeleteMapping("/eliminarReserva")
    public String eliminarReserva(@RequestParam int id) {
        return controladoraReserva.eliminarReserva(id);
    }

    @PostMapping("/crearViaje")
    public ResponseEntity<?> crearViaje(@RequestParam String fecha, @RequestParam String horaSalida, @RequestParam BigDecimal precio, @RequestParam String lugarSalida) {
        return controladoraViaje.crearViaje(fecha, horaSalida, precio, lugarSalida);
    }

    @GetMapping("/listarViajes")
    public List<Viaje> listarViajes() {
        return controladoraViaje.listarViajes();
    }

    @DeleteMapping("/eliminarViaje")
    public String eliminarViaje(@RequestParam int id) {
        return controladoraViaje.eliminarViaje(id);
    }

    @PostMapping("/actualizarViaje")
    public ResponseEntity<?> actualizarViaje(@RequestParam int id, @RequestParam String fecha, @RequestParam String horaSalida, @RequestParam BigDecimal precio, @RequestParam String lugarSalida, @RequestParam(required = false) String estado) {
        return controladoraViaje.actualizarViaje(id, fecha, horaSalida, precio, lugarSalida, estado);
    }

    @PostMapping("/crearAutomovil")
    public ResponseEntity<?> crearAutomovil(@RequestParam String placa, @RequestParam int capacidad, @RequestParam int modelo, @RequestParam String marca) {
        return controladoraAutomovil.crearAutomovil(placa, capacidad, modelo, marca);
    }

    @GetMapping("/listarAutomoviles")
    public List<Automovil> listarAutomoviles() {
        return controladoraAutomovil.listarAutomoviles();
    }

    @DeleteMapping("/eliminarAutomovil")
    public String eliminarAutomovil(@RequestParam int id) {
        return controladoraAutomovil.eliminarAutomovil(id);
    }

    @PostMapping("/actualizarAutomovil")
    public ResponseEntity<?> actualizarAutomovil(@RequestParam int id,@RequestParam String placa, @RequestParam int capacidad, @RequestParam int modelo, @RequestParam String marca) {
        return controladoraAutomovil.actualizarAutomovil(id, placa, capacidad, modelo, marca);
    }

    @PostMapping("/crearDestino")
    public String crearDestino(@RequestParam String nombre, @RequestParam String descripcion) {
        return controladoraDestino.crearDestino(nombre, descripcion);
    }

    @GetMapping("/listarDestinos")
    public List<Destino> listarDestinos() {
        return controladoraDestino.listarDestinos();
    }

    @DeleteMapping("/eliminarDestino")
    public String eliminarDestino(@RequestParam int id) {
        return controladoraDestino.eliminarDestino(id);
    }

    @PostMapping("/actualizarDestino")
    public String actualizarDestino(@RequestParam int id, @RequestParam String nombre, @RequestParam String descripcion) {
        return controladoraDestino.actualizarDestino(id, nombre, descripcion);
    }

    @PostMapping("/crearItinerario")
    public ResponseEntity<?> crearItinerario(@RequestParam int idViaje, @RequestParam int idDestino, @RequestParam short orden) {
        return controladoraItinerario.crearItinerario(idViaje, idDestino, orden);
    }

    @GetMapping("/listarItinerarios")
    public ResponseEntity<?> listarItinerarios(@RequestParam int idViaje) {
        return controladoraItinerario.listarDestinos(idViaje);
    }

    @DeleteMapping("/eliminarItinerario")
    public String eliminarItinerario(@RequestParam int idViaje) {
        return controladoraItinerario.eliminarItinerario(idViaje);
    }

    @PostMapping("/actualizarItinerario")
    public String actualizarItinerario(@RequestParam int idViaje, @RequestParam short ordenActual, @RequestParam short nuevoOrden) {
        return controladoraItinerario.actualizarItinerario(idViaje, ordenActual, nuevoOrden);
    }

    @GetMapping("/reservasDelDia")
    public List<Reserva> listarReservasDelDia() {
        LocalDate hoy = LocalDate.now();
        return repositorioReserva.findAll().stream().filter(r -> r.getFechaReserva().toLocalDate().equals(hoy)).collect(Collectors.toList());
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

    @GetMapping("/pasajerosViaje")
    public ResponseEntity<?> pasajerosPorViaje(@RequestParam("idViaje") int idViaje) {
        Optional<Viaje> viaje = repositorioViaje.findById(idViaje);
        if (viaje.isEmpty()) 
            return ResponseEntity.status(404).body("Viaje no existe");
        List<Map<String, Object>> pasajeros = new java.util.ArrayList<>();
        
        for (Reserva r : repositorioReserva.findByViajeId(idViaje)) {
            if (r.getEstado() == Reserva.EstadoReserva.pagada || 
                r.getEstado() == Reserva.EstadoReserva.finalizada) {
                Map<String, Object> p = new HashMap<>();
                p.put("nombre", r.getUsuario().getNombre());
                p.put("apellido", r.getUsuario().getApellido());
                p.put("asiento", r.getNumeroAsiento());
                pasajeros.add(p);
            }
        }
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("viajeId", idViaje);
        respuesta.put("fecha", viaje.get().getFecha().toString());
        respuesta.put("lugarSalida", viaje.get().getLugarSalida());
        respuesta.put("automovil", viaje.get().getAutomovil().getPlaca());
        respuesta.put("pasajeros", pasajeros);
        
        return ResponseEntity.ok(respuesta);
    }
}
