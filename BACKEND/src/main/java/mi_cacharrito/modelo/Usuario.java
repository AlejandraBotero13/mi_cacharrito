package mi_cacharrito.modelo;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @Column(name= "cc", length = 15, nullable = false)
    private String cc;

    @Column(name = "nombre", length = 45, nullable = false)
    private String nombre;

    @Column(name = "apellido", length = 45, nullable = false)
    private String apellido;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "telefono", length = 15, nullable = false)
    private String telefono;

    
    public Usuario(String cc, String nombre, String apellido, LocalDate fechaNacimiento, String telefono) {
        this.cc = cc;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
    }

    public Usuario() {

    }

    public String getCc() { 
        return cc; 
    }

    public void setCc(String cc) {
         this.cc = cc; 
    }

    public String getNombre() {
         return nombre; 
    }

    public void setNombre(String nombre) {
         this.nombre = nombre; 
    }

    public String getApellido() { 
        return apellido; 
    }

    public void setApellido(String apellido) { 
        this.apellido = apellido; 
    }

    public LocalDate getFechaNacimiento() { 
        return fechaNacimiento; 
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
         this.fechaNacimiento = fechaNacimiento; 
    }

    public String getTelefono() {
         return telefono; 
    }

    public void setTelefono(String telefono) {
         this.telefono = telefono; 
    }
}
