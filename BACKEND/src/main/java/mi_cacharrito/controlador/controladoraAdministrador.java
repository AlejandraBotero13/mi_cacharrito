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

}
