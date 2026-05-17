package mi_cacharrito.controlador;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
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

import mi_cacharrito.modelo.Administrador;
import mi_cacharrito.modelo.Automovil;
import mi_cacharrito.modelo.Destino;
import mi_cacharrito.modelo.Itinerario;
import mi_cacharrito.modelo.Reserva;
import mi_cacharrito.modelo.Usuario;
import mi_cacharrito.modelo.Viaje;
import mi_cacharrito.repositorio.administrador;
import mi_cacharrito.repositorio.automovil;
import mi_cacharrito.repositorio.destino;
import mi_cacharrito.repositorio.itinerario;
import mi_cacharrito.repositorio.reserva;
import mi_cacharrito.repositorio.usuario;
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

    @Autowired 
    private automovil repositorioAutomovil;

    @Autowired 
    private destino repositorioDestino;

    @Autowired 
    private itinerario repositorioItinerario;

    @Autowired 
    private usuario repositorioUsuario;

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

    // ========== VIAJES ==========
    @PostMapping("/crearViaje")
    public ResponseEntity<?> crearViaje(@RequestParam("fecha") String fecha, @RequestParam("horaSalida") String horaSalida, @RequestParam("precio") BigDecimal precio, @RequestParam("lugarSalida") String lugarSalida) {
        Viaje v = new Viaje();
        v.setFecha(LocalDate.parse(fecha));
        v.setHoraSalida(LocalTime.parse(horaSalida));
        v.setPrecio(precio);
        v.setEstado(Viaje.EstadoViaje.activo);
        repositorioViaje.save(v);
        return ResponseEntity.ok(v);
    }

    @GetMapping("/listarViajes")
    public List<Viaje> listarViajes() {
        return repositorioViaje.findAll();
    }

    @PostMapping("/actualizarViaje")
    public ResponseEntity<?> actualizarViaje(@RequestBody Viaje viaje) {
        if (!repositorioViaje.existsById(viaje.getId()))
            return ResponseEntity.status(404).body("Viaje no existe");
        repositorioViaje.save(viaje);
        return ResponseEntity.ok(viaje);
    }

    @DeleteMapping("/eliminarViaje")
    public String eliminarViaje(@RequestParam("id") int id) {
        if (!repositorioViaje.existsById(id)) return "Viaje no existe";
        repositorioViaje.deleteById(id);
        return "Viaje eliminado";
    }

    // ========== RESERVAS ==========
    @PostMapping("/crearReserva")
    public ResponseEntity<?> crearReserva(@RequestParam("numAsiento") int numAsiento, @RequestParam("idViaje") int idViaje, @RequestParam("ccUsuario") String ccUsuario, @RequestParam("idAdmin") int idAdmin) {
        Optional<Viaje> viaje = repositorioViaje.findById(idViaje);
        Optional<Usuario> usuario = repositorioUsuario.findById(ccUsuario);
        Optional<Administrador> admin = repositorioAdministrador.findById(idAdmin);
        if (viaje.isEmpty() || usuario.isEmpty() || admin.isEmpty())
            return ResponseEntity.status(404).body("Viaje, Usuario o Administrador no encontrado");
        Reserva r = new Reserva();
        r.setNumeroAsiento(numAsiento);
        r.setFechaReserva(java.time.LocalDateTime.now());
        r.setEstado(Reserva.EstadoReserva.pendiente);
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

    @PostMapping("/actualizarReserva")
    public ResponseEntity<?> actualizarReserva(@RequestBody Reserva reserva) {
        if (!repositorioReserva.existsById(reserva.getId()))
            return ResponseEntity.status(404).body("Reserva no existe");
        repositorioReserva.save(reserva);
        return ResponseEntity.ok(reserva);
    }

    @DeleteMapping("/eliminarReserva")
    public String eliminarReserva(@RequestParam("id") int id) {
        if (!repositorioReserva.existsById(id)) return "Reserva no existe";
        repositorioReserva.deleteById(id);
        return "Reserva eliminada";
    }

    // ========== AUTOMÓVILES ==========
    @PostMapping("/crearAutomovil")
    public ResponseEntity<Automovil> crearAutomovil(@RequestParam("placa") String placa, @RequestParam("capacidad") int capacidad, @RequestParam("modelo") int modelo, @RequestParam("marca") String marca) {
        Automovil a = new Automovil();
        a.setPlaca(placa);
        a.setCapacidad(capacidad);
        a.setModelo(modelo);
        a.setMarca(marca);
        repositorioAutomovil.save(a);
        return ResponseEntity.ok(a);
    }

    @GetMapping("/listarAutomoviles")
    public List<Automovil> listarAutomoviles() {
        return repositorioAutomovil.findAll();
    }

    @PostMapping("/actualizarAutomovil")
    public ResponseEntity<?> actualizarAutomovil(@RequestBody Automovil auto) {
        if (!repositorioAutomovil.existsById(auto.getId()))
            return ResponseEntity.status(404).body("Automóvil no existe");
        repositorioAutomovil.save(auto);
        return ResponseEntity.ok(auto);
    }

    @DeleteMapping("/eliminarAutomovil")
    public String eliminarAutomovil(@RequestParam("id") int id) {
        if (!repositorioAutomovil.existsById(id)) return "Automóvil no existe";
        repositorioAutomovil.deleteById(id);
        return "Automóvil eliminado";
    }

    // ========== DESTINOS ==========
    @PostMapping("/crearDestino")
    public ResponseEntity<Destino> crearDestino(@RequestParam("nombre") String nombre, @RequestParam("descripcion") String descripcion) {
        Destino d = new Destino();
        d.setNombre(nombre);
        d.setDescripcion(descripcion);
        repositorioDestino.save(d);
        return ResponseEntity.ok(d);
    }

    @GetMapping("/listarDestinos")
    public List<Destino> listarDestinos() {
        return repositorioDestino.findAll();
    }

    @PostMapping("/actualizarDestino")
    public ResponseEntity<?> actualizarDestino(@RequestParam("id") int id, @RequestParam("nombre") String nombre, @RequestParam("descripcion") String descripcion) {
        Optional<Destino> opt = repositorioDestino.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("No existe destino con id: " + id);
        }
        Destino d = opt.get();
        d.setNombre(nombre);
        d.setDescripcion(descripcion);
        repositorioDestino.save(d);
        return ResponseEntity.ok(d);
    }

    @DeleteMapping("/eliminarDestino")
    public String eliminarDestino(@RequestParam("id") int id) {
        if (!repositorioDestino.existsById(id)) return "Destino no existe";
        repositorioDestino.deleteById(id);
        return "Destino eliminado";
    }

    // ========== ITINERARIOS ==========
    @PostMapping("/crearItinerario")
    public ResponseEntity<?> crearItinerario(@RequestParam("idViaje") int idViaje) {
        Optional<Viaje> viaje = repositorioViaje.findById(idViaje);
        if (viaje.isEmpty()) return ResponseEntity.status(404).body("Viaje no existe");
        // Crear itinerario vacío (normalmente se crea al agregar destinos)
        return ResponseEntity.ok("Itinerario creado (use agregarDestino)");
    }

    @GetMapping("/listarItinerarios")
    public List<Itinerario> listarItinerarios() {
        return repositorioItinerario.findAll();
    }

    @PostMapping("/actualizarItinerario")
    public ResponseEntity<?> actualizarItinerario(@RequestBody Itinerario itinerario) {
        // La clave compuesta hace complicado el update directo, se delega a métodos específicos
        return ResponseEntity.ok("Use /agregarDestino, /eliminarDestino, /actualizarOrden");
    }

    @DeleteMapping("/eliminarItinerario")
    public String eliminarItinerario(@RequestParam("idViaje") int idViaje) {
        List<Itinerario> lista = repositorioItinerario.findByViaje_Id(idViaje);
        if (lista.isEmpty()) return "No existe itinerario para ese viaje";
        repositorioItinerario.deleteAll(lista);
        return "Itinerario eliminado";
    }

    // ========== REPORTES ==========
    @GetMapping("/generarReporte")
    public String generarReporte() {
        // Lógica para generar reporte (ej. cantidad de reservas por día, ingresos, etc.)
        long totalReservas = repositorioReserva.count();
        long totalViajes = repositorioViaje.count();
        return "Reporte: Total reservas = " + totalReservas + ", Total viajes = " + totalViajes;
    }
}