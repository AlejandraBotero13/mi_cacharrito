package mi_cacharrito.repositorio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mi_cacharrito.modelo.Reserva;

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

    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE r.viaje.id = :viajeId AND r.numeroAsiento = :asiento AND r.estado IN ('pendiente', 'pagada')")
    boolean existsByViajeYAsiento(@Param("viajeId") int viajeId, @Param("asiento") int asiento);

    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE r.viaje.id = :viajeId AND r.numeroAsiento = :asiento AND r.estado IN ('pendiente', 'pagada') AND r.id != :idReserva")
    boolean existsByViajeYAsientoActualizar(@Param("viajeId") int viajeId, @Param("asiento") int asiento, @Param("idReserva") int idReserva);
}