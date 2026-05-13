package mi_cacharrito.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "itinerario")
public class Itinerario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Column(name = "orden_visita", nullable = false)
    private short ordenVisita;

    @ManyToOne
    @JoinColumn(name = "destino_id", nullable = false)
    private Destino destino;

    @ManyToOne
    @JoinColumn(name = "viaje_id", nullable = false)
    private Viaje viaje;

   

    public Itinerario(int id, short ordenVisita, Destino destino, Viaje viaje) {
        this.id = id;
        this.ordenVisita = ordenVisita;
        this.destino = destino;
        this.viaje = viaje;
    }


     public Itinerario() {
 
     }


    public int getId() {
         return id; }


    public void setId(int id) { 
        this.id = id; }



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
