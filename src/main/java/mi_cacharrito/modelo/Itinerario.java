package mi_cacharrito.modelo;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "itinerario")
@IdClass(Itinerario.ItinerarioId.class)
public class Itinerario {

    public static class ItinerarioId implements Serializable {
        private int viaje;
        private short ordenVisita;

        public ItinerarioId() {}

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ItinerarioId)) return false;
            ItinerarioId that = (ItinerarioId) o;
            return ordenVisita == that.ordenVisita && viaje == that.viaje;
        }

        @Override
        public int hashCode() {
            return Objects.hash(viaje, ordenVisita);
        }
    }

    @Id
    @ManyToOne
    @JoinColumn(name = "viaje_id", nullable = false)
    private Viaje viaje;

    @Id
    @Column(name = "orden_visita", nullable = false)
    private short ordenVisita;

    @ManyToOne
    @JoinColumn(name = "destino_id", nullable = false)
    private Destino destino;

    public Itinerario(short ordenVisita, Destino destino, Viaje viaje) {
        this.ordenVisita = ordenVisita;
        this.destino = destino;
        this.viaje = viaje;
    }

    public Itinerario() {}

    public short getOrdenVisita() { 
        return ordenVisita; 
    }

    public void setOrdenVisita(short ordenVisita) { 
        this.ordenVisita = ordenVisita; 
    }

    public Destino getDestino() { 
        return destino; 
    }
    
    public void setDestino(Destino destino) { 
        this.destino = destino; 
    }

    public Viaje getViaje() { 
        return viaje; 
    }

    public void setViaje(Viaje viaje) { 
        this.viaje = viaje; 
    }
}