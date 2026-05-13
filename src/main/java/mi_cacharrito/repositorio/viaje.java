package mi_cacharrito.repositorio;

import java.util.List;
import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mi_cacharrito.modelo.Viaje;

@Repository
public interface viaje extends JpaRepository<Viaje, Integer> {
    public List<Viaje> findByFecha(Date fecha);
    public List<Viaje> findByHoraSalida(short horaSalida);
    public List<Viaje> findByPrecio(float precio);
    public List<Viaje> findByEstado(short estado);
    public List<Viaje> findByAutomovil(int automovil);

}