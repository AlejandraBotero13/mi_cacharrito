package mi_cacharrito.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import mi_cacharrito.modelo.Automovil;
import java.util.List;


@Repository
public interface automovil extends JpaRepository<Automovil, Integer> {
    public List<Automovil> findByPlaca(String placa);
    public List<Automovil> findByMarca(String marca);
    public List<Automovil> findByModelo(String modelo);
    public List<Automovil> findByCapacidad(String capacidad);

}