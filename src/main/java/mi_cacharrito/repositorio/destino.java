package mi_cacharrito.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mi_cacharrito.modelo.Destino;

@Repository
public interface destino extends JpaRepository<Destino, Integer> {
    public List<Destino> findByNombre(String nombre);
    public List<Destino> findByDescripcion(String descripcion);

}
