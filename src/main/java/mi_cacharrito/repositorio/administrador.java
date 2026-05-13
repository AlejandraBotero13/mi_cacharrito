package mi_cacharrito.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mi_cacharrito.modelo.Administrador;

@Repository
public interface administrador extends JpaRepository<Administrador, Integer> {
    public List<Administrador> findByNombre(String nombre);
    public List<Administrador> findByContraseña(String contraseña);
    public List<Administrador> findByUsuario(String usuario);

}


