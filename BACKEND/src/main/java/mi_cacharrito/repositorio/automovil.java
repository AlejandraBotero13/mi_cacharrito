package mi_cacharrito.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mi_cacharrito.modelo.Automovil;

@Repository
public interface automovil extends JpaRepository<Automovil, Integer> {
    List<Automovil> findByPlaca(String placa);
    List<Automovil> findByCapacidad(int  capacidad);
    List<Automovil> findByModelo(int modelo);
    List<Automovil> findByMarca(String marca);

}