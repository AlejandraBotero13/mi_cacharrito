package mi_cacharrito.controlador;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<?> crearViaje(@RequestParam("fecha") String fecha, @RequestParam("horaSalida") String horaSalida, @RequestParam("precio") BigDecimal precio, @RequestParam("lugarSalida") String lugarSalida,@RequestParam("idAuto") int idAuto) {

            Optional<Automovil> auto = repositorioAutomovil.findById(idAuto);
    if (auto.isEmpty()) {
        return ResponseEntity.status(404).body("Automóvil no existe con id: " + idAuto);
    }

        Viaje v = new Viaje();
        v.setFecha(LocalDate.parse(fecha));
        v.setHoraSalida(LocalTime.parse(horaSalida));
        v.setPrecio(precio);
        v.setLugarSalida(lugarSalida);
        v.setAutomovil(auto.get());
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
    if (!repositorioViaje.existsById(id)) {
        return "Viaje no existe";
    }

    List<Itinerario> itinerarios = repositorioItinerario.findByViaje_Id(id);
    if (!itinerarios.isEmpty()) {
        repositorioItinerario.deleteAll(itinerarios);
    }

    List<Reserva> reservas = repositorioReserva.findByViajeId(id);
    if (!reservas.isEmpty()) {
        boolean todasPermitidas = reservas.stream()
                .allMatch(r -> r.getEstado() == Reserva.EstadoReserva.finalizada ||
                               r.getEstado() == Reserva.EstadoReserva.cancelada);
        if (!todasPermitidas) {
            return "No se puede eliminar el viaje porque tiene reservas pendientes o pagadas. " +
                   "Debe cancelarlas o esperar a que finalicen.";
        }
        for (Reserva r : reservas) {
            r.setViaje(null);
            repositorioReserva.save(r);
        }
    }

    repositorioViaje.deleteById(id);
    return "Viaje eliminado correctamente. Se desvincularon " + reservas.size() + " reservas (no eliminadas).";
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
    public ResponseEntity<?> crearItinerario(@RequestParam("idViaje") int idViaje, @RequestParam("idDestino") int idDestino, @RequestParam("orden") short orden) {
        Optional<Viaje> viaje = repositorioViaje.findById(idViaje);
        Optional<Destino> destino = repositorioDestino.findById(idDestino);
        if (viaje.isEmpty() || destino.isEmpty()) {
            return ResponseEntity.status(404).body("Viaje o Destino no existe");
        }
        Itinerario it = new Itinerario(orden, destino.get(), viaje.get());
        repositorioItinerario.save(it);
        return ResponseEntity.ok(it);
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
public ResponseEntity<Map<String, Object>> generarReporte() {
    Map<String, Object> reporte = new HashMap<>();

    // ===== DATOS EXISTENTES =====
    List<Reserva> todasReservas = repositorioReserva.findAll();
    reporte.put("totalReservas", todasReservas.size());
    reporte.put("reservasPorUsuario", todasReservas.stream()
        .collect(Collectors.groupingBy(r -> r.getUsuario().getCc(), Collectors.counting())));
    reporte.put("reservasPorAdministrador", todasReservas.stream()
        .filter(r -> r.getAdministrador() != null)
        .collect(Collectors.groupingBy(r -> r.getAdministrador().getId(), Collectors.counting())));

    List<Viaje> todosViajes = repositorioViaje.findAll();
    reporte.put("totalViajes", todosViajes.size());
    reporte.put("viajesPorEstado", todosViajes.stream()
        .collect(Collectors.groupingBy(Viaje::getEstado, Collectors.counting())));

    reporte.put("totalAutomoviles", repositorioAutomovil.count());
    reporte.put("totalDestinos", repositorioDestino.count());
    reporte.put("totalItinerarios", repositorioItinerario.count());

    // ===== NUEVAS MÉTRICAS =====

    // 1. Ingresos totales (solo reservas pagadas o finalizadas)
    BigDecimal ingresosTotales = todasReservas.stream()
        .filter(r -> r.getEstado() == Reserva.EstadoReserva.pagada || 
                     r.getEstado() == Reserva.EstadoReserva.finalizada)
        .map(Reserva::getTotalPagar)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    reporte.put("ingresosTotales", ingresosTotales);

    // 2. Reservas por estado
    Map<Reserva.EstadoReserva, Long> reservasPorEstado = todasReservas.stream()
        .collect(Collectors.groupingBy(Reserva::getEstado, Collectors.counting()));
    reporte.put("reservasPorEstado", reservasPorEstado);

    // 3. Top 5 destinos más visitados (según itinerarios)
    List<Itinerario> todosItinerarios = repositorioItinerario.findAll();
    Map<String, Long> destinosCount = todosItinerarios.stream()
        .map(it -> it.getDestino().getNombre())
        .collect(Collectors.groupingBy(nombre -> nombre, Collectors.counting()));
    List<Map.Entry<String, Long>> topDestinos = destinosCount.entrySet().stream()
        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
        .limit(5)
        .collect(Collectors.toList());
    reporte.put("topDestinos", topDestinos);

    // 4. Utilización de automóviles (viajes por auto)
    Map<Integer, Long> viajesPorAuto = todosViajes.stream()
        .filter(v -> v.getAutomovil() != null)
        .collect(Collectors.groupingBy(v -> v.getAutomovil().getId(), Collectors.counting()));
    reporte.put("viajesPorAutomovil", viajesPorAuto);

    // 5. Próximos viajes activos (fecha posterior a hoy)
    LocalDate hoy = LocalDate.now();
    long proximosViajes = todosViajes.stream()
        .filter(v -> v.getEstado() == Viaje.EstadoViaje.activo && v.getFecha().isAfter(hoy))
        .count();
    reporte.put("proximosViajesActivos", proximosViajes);

    // 6. Tasa de cancelación de reservas
    long canceladas = reservasPorEstado.getOrDefault(Reserva.EstadoReserva.cancelada, 0L);
    double tasaCancelacion = todasReservas.isEmpty() ? 0 : (double) canceladas / todasReservas.size();
    reporte.put("tasaCancelacionReservas", tasaCancelacion);

    // 7. Clientes frecuentes (más de 3 reservas)
    Map<String, Long> reservasPorUsuarioMap = todasReservas.stream()
        .collect(Collectors.groupingBy(r -> r.getUsuario().getCc(), Collectors.counting()));
    List<String> clientesFrecuentes = reservasPorUsuarioMap.entrySet().stream()
        .filter(e -> e.getValue() > 3)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
    reporte.put("clientesFrecuentes", clientesFrecuentes);

    return ResponseEntity.ok(reporte);
}
}