package mi_cacharrito.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "viaje")
public class Viaje {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_salida", nullable = false)
    private LocalTime horaSalida;

    @Column(name = "precio", nullable = false)
    private BigDecimal precio;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoViaje estado;

    @ManyToOne
    @JoinColumn(name = "automovil_id", nullable = false)
    private Automovil automovil;

    public enum EstadoViaje {
        activo, cancelado, finalizado,
    }

    

    public Viaje(int id, LocalDate fecha, LocalTime horaSalida, BigDecimal precio, EstadoViaje estado, Automovil automovil) {
        this.id = id;
        this.fecha = fecha;
        this.horaSalida = horaSalida;
        this.precio = precio;
        this.estado = estado;
        this.automovil = automovil;
    }


    public Viaje() {}

    public int getId() { 
        return id; }


    public void setId(int id) {
         this.id = id; }


    public LocalDate getFecha() { 
        return fecha; }


    public void setFecha(LocalDate fecha) {
         this.fecha = fecha; }


    public LocalTime getHoraSalida() {
         return horaSalida; }


    public void setHoraSalida(LocalTime horaSalida) { 
        this.horaSalida = horaSalida; }


    public BigDecimal getPrecio() { 
        return precio; }


    public void setPrecio(BigDecimal precio) { 
        this.precio = precio; }

    public EstadoViaje getEstado() { 
        return estado; }

    public void setEstado(EstadoViaje estado) { 
        this.estado = estado; }


    public Automovil getAutomovil() {
         return automovil; }


    public void setAutomovil(Automovil automovil) {
         this.automovil = automovil; }


}
