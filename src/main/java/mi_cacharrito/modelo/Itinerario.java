package mi_cacharrito.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "itinerario")
public class Itinerario {


    @Column(name = "orden_visita", nullable = false)
    private short ordenVisita;

    @ManyToOne
    @JoinColumn(name = "destino_id", nullable = false)
    private Destino destino;

    @ManyToOne
    @JoinColumn(name = "viaje_id", nullable = false)
    private Viaje viaje;

   

    public Itinerario(short ordenVisita, Destino destino, Viaje viaje) {
        this.ordenVisita = ordenVisita;
        this.destino = destino;
        this.viaje = viaje;
    }


     public Itinerario() {
 
     }


    public short getOrdenVisita() { 
        return ordenVisita; }


    public void setOrdenVisita(short ordenVisita) { 
        this.ordenVisita = ordenVisita; }



    public Destino getDestino() { 
        return destino; }


    public void setDestino(Destino destino) { 
        this.destino = destino; }



    public Viaje getViaje() { 
        return viaje; }


    public void setViaje(Viaje viaje) {
         this.viaje = viaje; }
         
}
