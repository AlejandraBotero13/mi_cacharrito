package mi_cacharrito.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "administrador")
public class Administrador {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name="nombre", length=45, nullable=false)
    private String nombre;

    @Column(name="contraseña", length=45, nullable=false)
    private String contraseña;

    @Column(name="usuario", length=45, nullable=false)
    private String usuario;

    public Administrador(int id, String nombre, String contraseña, String usuario) {
        this.id = id;
        this.nombre = nombre;
        this.contraseña = contraseña;
        this.usuario = usuario;
    }

    public Administrador() {
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
