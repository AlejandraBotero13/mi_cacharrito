package mi_cacharrito.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mi_cacharrito.modelo.Itinerario;

@Repository
public interface itinerario extends JpaRepository<Itinerario, Itinerario.ItinerarioId> {

    List<Itinerario> findByDestino_Id(int destinoId);
    List<Itinerario> findByViaje_Id(int viajeId);
    List<Itinerario> findByOrdenVisita(short ordenVisita);

    @Query("SELECT i FROM Itinerario i JOIN FETCH i.destino JOIN FETCH i.viaje ORDER BY i.viaje.id, i.ordenVisita")
    List<Itinerario> findAllOrdenadoPorViajeYOrden();

    @Query("SELECT i FROM Itinerario i JOIN FETCH i.destino JOIN FETCH i.viaje WHERE i.viaje.id = :idViaje ORDER BY i.ordenVisita")
    List<Itinerario> findByViajeOrdenado(@Param("idViaje") int idViaje);

    @Query("SELECT i FROM Itinerario i JOIN FETCH i.destino JOIN FETCH i.viaje WHERE i.viaje.id = :idViaje AND i.ordenVisita = :orden")
    Optional<Itinerario> findByViajeIdAndOrden(@Param("idViaje") int idViaje, @Param("orden") short orden);

    @Query("SELECT i FROM Itinerario i JOIN FETCH i.destino JOIN FETCH i.viaje WHERE i.destino.id = :idDestino")
    List<Itinerario> findByDestinoOrdenado(@Param("idDestino") int idDestino);
}