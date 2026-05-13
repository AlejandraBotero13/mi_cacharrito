package mi_cacharrito.modelo;

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
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Column(name = "numero_asiento", nullable = false)
    private int numeroAsiento;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDateTime fechaReserva;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoReserva estado;

    @ManyToOne
    @JoinColumn(name = "usuario_cc", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "viaje_id", nullable = false)
    private Viaje viaje;

    @ManyToOne
    @JoinColumn(name = "administrador_id", nullable = false)
    private Administrador administrador;

    @Column(name = "total_pagar", nullable = false)
    private BigDecimal totalPagar;

    public enum EstadoReserva { pagada, pendiente, cancelada
    }

   

    public Reserva(int id, int numeroAsiento, LocalDateTime fechaReserva, EstadoReserva estado,
                   Usuario usuario, Viaje viaje, Administrador administrador, BigDecimal totalPagar) {
                    
        this.id = id;
        this.numeroAsiento = numeroAsiento;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
        this.usuario = usuario;
        this.viaje = viaje;
        this.administrador = administrador;
        this.totalPagar = totalPagar;
    }

     public Reserva() {

     }

    public int getId() {
         return id; }


    public void setId(int id) { 
        this.id = id; }


    public int getNumeroAsiento() {
         return numeroAsiento; }


    public void setNumeroAsiento(int numeroAsiento) { 
        this.numeroAsiento = numeroAsiento; }


    public LocalDateTime getFechaReserva() {
         return fechaReserva; }


    public void setFechaReserva(LocalDateTime fechaReserva) {
         this.fechaReserva = fechaReserva; }


    public EstadoReserva getEstado() { 
        return estado; }


    public void setEstado(EstadoReserva estado) { 
        this.estado = estado; }


    public Usuario getUsuario() { 
        return usuario; }


    public void setUsuario(Usuario usuario) { 
        this.usuario = usuario; }


    public Viaje getViaje() {
         return viaje; }


    public void setViaje(Viaje viaje) { 
        this.viaje = viaje; }


    public Administrador getAdministrador() {
         return administrador; }


    public void setAdministrador(Administrador administrador) {
         this.administrador = administrador; }


    public BigDecimal getTotalPagar() { 
        return totalPagar; }


    public void setTotalPagar(BigDecimal totalPagar) { 
        this.totalPagar = totalPagar; }



}