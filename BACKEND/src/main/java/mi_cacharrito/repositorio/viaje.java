package mi_cacharrito.repositorio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mi_cacharrito.modelo.Viaje;

@Repository
public interface viaje extends JpaRepository<Viaje, Integer> {
    public List<Viaje> findByFecha(LocalDate fecha);
    public List<Viaje> findByHoraSalida(LocalTime horaSalida);
    public List<Viaje> findByPrecio(BigDecimal  precio);
    public List<Viaje> findByEstado(Viaje.EstadoViaje estado);
    public List<Viaje> findByLugarSalida(String lugarSalida);
    List<Viaje> findByAutomovilId(int automovilId);
    
    @Query("SELECT DISTINCT v FROM Viaje v JOIN v.itinerarios i WHERE v.fecha = :fecha AND i.destino.id = :destinoId")
    List<Viaje> findByFechaYDestino(@Param("fecha") LocalDate fecha, @Param("destinoId") int destinoId);
}