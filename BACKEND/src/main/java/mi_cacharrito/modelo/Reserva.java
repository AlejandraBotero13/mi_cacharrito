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
    private Usuario usuario_cc;

    @ManyToOne
    @JoinColumn(name = "viaje_id", nullable = false)
    private Viaje viaje_id;

    @ManyToOne
    @JoinColumn(name = "administrador_id", nullable = false)
    private Administrador administrador_id;

    @Column(name = "total_pagar", nullable = false)
    private BigDecimal totalPagar;

    public enum EstadoReserva { pagada, pendiente, cancelada
    }

   

    public Reserva(int id, int numeroAsiento, LocalDateTime fechaReserva, EstadoReserva estado, Usuario usuario, Viaje viaje, Administrador administrador, BigDecimal totalPagar) {
                    
        this.id = id;
        this.numeroAsiento = numeroAsiento;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
        this.usuario_cc = usuario;
        this.viaje_id = viaje;
        this.administrador_id = administrador;
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
        return usuario_cc; }


    public void setUsuario(Usuario usuario) { 
        this.usuario_cc = usuario; }


    public Viaje getViaje() {
         return viaje_id    ; }


    public void setViaje(Viaje viaje) { 
        this.viaje_id = viaje; }


    public Administrador getAdministrador() {
         return administrador_id; }


    public void setAdministrador(Administrador administrador) {
         this.administrador_id = administrador; }


    public BigDecimal getTotalPagar() { 
        return totalPagar; }


    public void setTotalPagar(BigDecimal totalPagar) { 
        this.totalPagar = totalPagar; }



}