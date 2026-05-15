package mi_cacharrito.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

import mi_cacharrito.modelo.Reserva;
import mi_cacharrito.modelo.Usuario;


@Repository
public interface usuario extends JpaRepository<Usuario, String> {
    public List<Usuario> findByCc(String cc);
    public List<Usuario> findByNombre(String nombre);
    public List<Usuario> findByApellido(String apellido);
    public List<Usuario> findByFechaNacimiento(LocalDate fechaNacimiento);
    public List<Usuario> findByTelefono(String telefono);
  
}