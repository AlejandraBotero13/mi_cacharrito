package mi_cacharrito.repositorio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import mi_cacharrito.modelo.Reserva;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface reserva extends JpaRepository<Reserva, Integer> {
    
    // Métodos correctamente nombrados según los atributos de la entidad Reserva
    public List<Reserva> findByUsuarioCc(String cc);
    public List<Reserva> findByViajeId(int viajeId);
    public List<Reserva> findByAdministradorId(int administradorId);
    public List<Reserva> findByEstado(Reserva.EstadoReserva estado);
    public List<Reserva> findByFechaReserva(LocalDateTime fechaReserva);
    public List<Reserva> findByNumeroAsiento(int numeroAsiento);
    public List<Reserva> findByTotalPagar(BigDecimal totalPagar);
    
    @Query("SELECT SUM(r.totalPagar) FROM Reserva r WHERE r.id = :id")
    BigDecimal calcularTotal(@Param("id") int id);
}