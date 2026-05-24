package mi_cacharrito.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mi_cacharrito.modelo.Destino;
import mi_cacharrito.modelo.Itinerario;
import mi_cacharrito.repositorio.destino;

@RestController
@RequestMapping("/destino/d/")
@CrossOrigin(origins = "http://localhost:4200")
public class controladoraDestino {

    @Autowired 
    private destino repositorioDestino;

    @Autowired
    private mi_cacharrito.repositorio.itinerario repositorioItinerario;

    @PostMapping("/crearDestino")
    public String crearDestino(@RequestParam("nombre") String nombre, @RequestParam("descripcion") String descripcion) {
        Destino d = new Destino();
        d.setNombre(nombre);
        d.setDescripcion(descripcion);
        repositorioDestino.save(d);
        return "Destino creado exitosamente";
    }

    @GetMapping("/listarDestinos")
    public List<Destino> listarDestinos() {
        return repositorioDestino.findAll();
    }

    @DeleteMapping("/eliminarDestino")
    public String eliminarDestino(@RequestParam("id") int id) {
        if (!repositorioDestino.existsById(id))
            return "No existe un destino con el id: " + id;

        List<Itinerario> itinerarios = repositorioItinerario.findByDestino_Id(id);
        if (!itinerarios.isEmpty()) {
            return "No se puede eliminar el destino porque tiene " + itinerarios.size() + " itinerarios asociados. Elimine primero los itinerarios.";
        }

        repositorioDestino.deleteById(id);
        return "Destino eliminado exitosamente";
    }

    @PostMapping("/actualizarDestino")
    public String actualizarDestino(@RequestParam("id") int id, @RequestParam("nombre") String nombre, @RequestParam("descripcion") String descripcion) {
        if (!repositorioDestino.existsById(id))
            return "No existe un destino con el id: " + id;
        Destino d = repositorioDestino.findById(id).get();
        d.setNombre(nombre);
        d.setDescripcion(descripcion);
        repositorioDestino.save(d);
        return "Destino actualizado exitosamente";
    }

    @GetMapping("/obtenerInformacion")
    public String obtenerInformacion(@RequestParam("id") int id) {
        if (!repositorioDestino.existsById(id))
            return "No existe un destino con el id: " + id;
        Destino d = repositorioDestino.findById(id).get();
        return d.getNombre() + ": " + d.getDescripcion();
    }
}