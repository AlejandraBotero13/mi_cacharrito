package mi_cacharrito.repositorio;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mi_cacharrito.modelo.Viaje;

@Repository
public interface viaje extends JpaRepository<Viaje, Integer> {
    public List<Viaje> findByFecha(Date fecha);
    public List<Viaje> findByHoraSalida(LocalTime horaSalida);
    public List<Viaje> findByPrecio(BigDecimal  precio);
    public List<Viaje> findByEstado(Viaje.EstadoViaje estado);
    List<Viaje> findByAutomovilId(int automovilId);


}