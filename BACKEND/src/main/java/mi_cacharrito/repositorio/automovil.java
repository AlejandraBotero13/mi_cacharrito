package mi_cacharrito.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mi_cacharrito.modelo.Automovil;


@Repository
public interface automovil extends JpaRepository<Automovil, Integer> {
    public List<Automovil> findByPlaca(String placa);
    public List<Automovil> findByCapacidad(String capacidad);
    public List<Automovil> findByModelo(String modelo);
    public List<Automovil> findByMarca(String marca);

}