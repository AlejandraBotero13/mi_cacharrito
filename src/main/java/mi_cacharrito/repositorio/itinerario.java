package mi_cacharrito.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mi_cacharrito.modelo.Itinerario;

@Repository
public interface itinerario extends JpaRepository<Itinerario, Itinerario.ItinerarioId> {
    public List<Itinerario> findByDestinoId(int destino);
    public List<Itinerario> findByViajeId(int viaje);
    public List<Itinerario> findByOrdenVisita(short ordenVisita);
}
